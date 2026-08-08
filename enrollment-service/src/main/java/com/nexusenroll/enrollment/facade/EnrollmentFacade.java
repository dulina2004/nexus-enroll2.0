package com.nexusenroll.enrollment.facade;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.client.CourseServiceClient;
import com.nexusenroll.enrollment.client.NotificationClient;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.EnrollmentHistory;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import com.nexusenroll.enrollment.repository.EnrollmentHistoryRepository;
import com.nexusenroll.enrollment.repository.EnrollmentRepository;
import com.nexusenroll.enrollment.repository.WaitlistRepository;
import com.nexusenroll.enrollment.strategy.EnrollmentValidationContext;
import com.nexusenroll.enrollment.strategy.EnrollmentValidationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Facade pattern owning enrollment orchestration and transactional atomic boundaries.
 */
@Component
public class EnrollmentFacade {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentHistoryRepository historyRepository;
    private final WaitlistRepository waitlistRepository;
    private final CourseServiceClient courseServiceClient;
    private final NotificationClient notificationClient;
    private final List<EnrollmentValidationStrategy> validationStrategies;

    public EnrollmentFacade(EnrollmentRepository enrollmentRepository,
                            EnrollmentHistoryRepository historyRepository,
                            WaitlistRepository waitlistRepository,
                            CourseServiceClient courseServiceClient,
                            NotificationClient notificationClient,
                            List<EnrollmentValidationStrategy> validationStrategies) {
        this.enrollmentRepository = enrollmentRepository;
        this.historyRepository = historyRepository;
        this.waitlistRepository = waitlistRepository;
        this.courseServiceClient = courseServiceClient;
        this.notificationClient = notificationClient;
        this.validationStrategies = validationStrategies;
    }

    @Transactional
    public Enrollment enroll(long studentId, long sectionId) throws ServiceException {
        return enrollInternal(studentId, sectionId, this.validationStrategies, "SYSTEM", "Enrollment created");
    }

    @Transactional
    public Enrollment enrollWithOverride(long studentId, long sectionId, Long adminUserId, String reason) throws ServiceException {
        String actor = adminUserId != null ? "ADMIN:" + adminUserId : "ADMIN";
        String r = (reason != null && !reason.isBlank()) ? "Admin override: " + reason : "Admin override";
        // Override passes empty strategy list — demonstrates Strategy pattern swappability
        return enrollInternal(studentId, sectionId, Collections.emptyList(), actor, r);
    }

    private Enrollment enrollInternal(long studentId, long sectionId,
                                      List<EnrollmentValidationStrategy> strategies,
                                      String actor, String reason) throws ServiceException {
        if (studentId <= 0 || sectionId <= 0) {
            throw new ValidationException("Student ID and section ID are required");
        }

        // Duplicate enrollment check
        if (enrollmentRepository.findByStudentIdAndSectionId(studentId, sectionId).isPresent()) {
            throw new ValidationException("Student is already enrolled in this section");
        }

        // Fetch section snapshot
        CourseSectionSnapshot requestedSection = courseServiceClient.getSectionSnapshot(sectionId);

        // Fetch active sections for conflict checks
        List<Long> activeSectionIds = enrollmentRepository.findSectionIdsByStudentIdAndStatus(studentId, "ENROLLED");
        List<CourseSectionSnapshot> currentSections = courseServiceClient.getSectionSnapshots(activeSectionIds);

        // Fetch completed course codes for prerequisite checks
        List<Long> completedSectionIds = enrollmentRepository.findSectionIdsByStudentIdAndStatus(studentId, "COMPLETED");
        List<String> completedCourseCodes = courseServiceClient.getSectionSnapshots(completedSectionIds)
                .stream()
                .map(CourseSectionSnapshot::getCourseCode)
                .toList();

        Enrollment enrollment = new Enrollment(studentId, sectionId, LocalDate.now(), "ENROLLED");

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                enrollment,
                requestedSection,
                currentSections,
                completedCourseCodes
        );

        // Execute all validation strategies
        for (EnrollmentValidationStrategy strategy : strategies) {
            strategy.validate(context);
        }

        // Reserve seat in Course Service
        courseServiceClient.reserveSeat(sectionId);

        // Save enrollment
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Record history log
        historyRepository.save(new EnrollmentHistory(savedEnrollment.getId(), null, "ENROLLED", actor, reason));

        // Publish notification
        notificationClient.publishEnrollmentCreated(savedEnrollment, requestedSection);

        return savedEnrollment;
    }

    @Transactional
    public Enrollment drop(long enrollmentId) throws ServiceException {
        if (enrollmentId <= 0) {
            throw new ValidationException("Enrollment ID is required");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (!"ENROLLED".equalsIgnoreCase(enrollment.getStatus())) {
            throw new ValidationException("Only active enrollments can be dropped");
        }

        CourseSectionSnapshot section = courseServiceClient.getSectionSnapshot(enrollment.getSectionId());

        enrollment.setStatus("DROPPED");
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        historyRepository.save(new EnrollmentHistory(enrollmentId, "ENROLLED", "DROPPED", "SYSTEM", "Course dropped"));

        courseServiceClient.releaseSeat(section.getSectionId());

        notificationClient.publishEnrollmentDropped(updatedEnrollment, section);

        // Automatic promotion from waitlist within the same atomic transaction
        promoteFromWaitlist(section);

        return updatedEnrollment;
    }

    public List<Enrollment> getEnrollmentsForStudent(long studentId) throws ServiceException {
        if (studentId <= 0) {
            throw new ValidationException("Student ID is required");
        }
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional
    public WaitlistEntry joinWaitlist(long studentId, long sectionId) throws ServiceException {
        if (studentId <= 0 || sectionId <= 0) {
            throw new ValidationException("Student ID and section ID are required");
        }

        // Reject if already enrolled
        if (enrollmentRepository.findByStudentIdAndSectionId(studentId, sectionId).isPresent()) {
            throw new ValidationException("Student is already enrolled in this section");
        }

        // Reject duplicate waitlist entry
        if (waitlistRepository.findByStudentIdAndSectionId(studentId, sectionId).isPresent()) {
            throw new ValidationException("Student is already on waitlist for this section");
        }

        CourseSectionSnapshot section = courseServiceClient.getSectionSnapshot(sectionId);
        Integer nextPos = waitlistRepository.findNextWaitlistPosition(sectionId);

        WaitlistEntry entry = new WaitlistEntry(studentId, sectionId, nextPos, "WAITING");
        WaitlistEntry savedEntry = waitlistRepository.save(entry);

        notificationClient.publishWaitlistJoined(savedEntry, section);

        return savedEntry;
    }

    @Transactional
    public void cancelWaitlist(long waitlistId) throws ServiceException {
        if (waitlistId <= 0) {
            throw new ValidationException("Waitlist ID is required");
        }
        WaitlistEntry entry = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Waitlist entry not found: " + waitlistId));

        entry.setStatus("CANCELLED");
        waitlistRepository.save(entry);
    }

    public List<WaitlistEntry> getWaitlistForStudent(long studentId) throws ServiceException {
        if (studentId <= 0) {
            throw new ValidationException("Student ID is required");
        }
        return waitlistRepository.findByStudentId(studentId);
    }

    private void promoteFromWaitlist(CourseSectionSnapshot section) throws ServiceException {
        Optional<WaitlistEntry> nextWaiting = waitlistRepository.findFirstBySectionIdAndStatusOrderByPositionAsc(section.getSectionId(), "WAITING");
        if (nextWaiting.isPresent()) {
            WaitlistEntry entry = nextWaiting.get();
            entry.setStatus("ENROLLED");
            waitlistRepository.save(entry);

            courseServiceClient.reserveSeat(section.getSectionId());

            Enrollment enrollment = new Enrollment(entry.getStudentId(), entry.getSectionId(), LocalDate.now(), "ENROLLED");
            Enrollment saved = enrollmentRepository.save(enrollment);

            historyRepository.save(new EnrollmentHistory(saved.getId(), null, "ENROLLED", "WAITLIST_PROMOTION", "Promoted from waitlist"));

            notificationClient.publishWaitlistSeatAvailable(entry, section);
        }
    }
}
