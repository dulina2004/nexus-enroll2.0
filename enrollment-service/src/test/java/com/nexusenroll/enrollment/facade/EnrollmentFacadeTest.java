package com.nexusenroll.enrollment.facade;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.client.CourseServiceClient;
import com.nexusenroll.enrollment.client.NotificationClient;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import com.nexusenroll.enrollment.repository.EnrollmentHistoryRepository;
import com.nexusenroll.enrollment.repository.EnrollmentRepository;
import com.nexusenroll.enrollment.repository.WaitlistRepository;
import com.nexusenroll.enrollment.strategy.CapacityCheckStrategy;
import com.nexusenroll.enrollment.strategy.PrerequisiteCheckStrategy;
import com.nexusenroll.enrollment.strategy.TimeConflictCheckStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentFacadeTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EnrollmentHistoryRepository historyRepository;

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private NotificationClient notificationClient;

    private EnrollmentFacade enrollmentFacade;
    private CourseSectionSnapshot testSection;

    @BeforeEach
    void setUp() {
        enrollmentFacade = new EnrollmentFacade(
                enrollmentRepository,
                historyRepository,
                waitlistRepository,
                courseServiceClient,
                notificationClient,
                List.of(
                        new PrerequisiteCheckStrategy(),
                        new CapacityCheckStrategy(),
                        new TimeConflictCheckStrategy()
                )
        );

        testSection = new CourseSectionSnapshot();
        testSection.setSectionId(100L);
        testSection.setCourseCode("CS101");
        testSection.setCapacity(30);
        testSection.setEnrolledCount(10);
        testSection.setScheduleDays("MWF");
        testSection.setStartTime(Time.valueOf("09:00:00"));
        testSection.setEndTime(Time.valueOf("10:00:00"));
    }

    @Test
    void enroll_successful() throws ServiceException {
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "ENROLLED")).thenReturn(List.of());
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "COMPLETED")).thenReturn(List.of());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> {
            Enrollment e = i.getArgument(0);
            e.setId(10L);
            return e;
        });

        Enrollment result = enrollmentFacade.enroll(1L, 100L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("ENROLLED", result.getStatus());
        verify(courseServiceClient).reserveSeat(100L);
        verify(historyRepository).save(any());
        verify(notificationClient).publishEnrollmentCreated(any(), any());
    }

    @Test
    void enroll_duplicateEnrollment_throwsValidationException() {
        Enrollment existing = new Enrollment(1L, 100L, java.time.LocalDate.now(), "ENROLLED");
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.of(existing));

        assertThrows(ValidationException.class, () -> enrollmentFacade.enroll(1L, 100L));
    }

    @Test
    void enroll_courseNotFound_throwsResourceNotFoundException() throws ServiceException {
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 999L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(999L)).thenThrow(new ResourceNotFoundException("Course section not found: 999"));

        assertThrows(ResourceNotFoundException.class, () -> enrollmentFacade.enroll(1L, 999L));
    }

    @Test
    void enroll_prerequisiteFailure_throwsValidationException() throws ServiceException {
        testSection.setPrerequisites("CS200");
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "ENROLLED")).thenReturn(List.of());
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "COMPLETED")).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> enrollmentFacade.enroll(1L, 100L));
    }

    @Test
    void enroll_capacityFailure_throwsValidationException() throws ServiceException {
        testSection.setCapacity(30);
        testSection.setEnrolledCount(30);
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "ENROLLED")).thenReturn(List.of());
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "COMPLETED")).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> enrollmentFacade.enroll(1L, 100L));
    }

    @Test
    void enroll_timeConflict_throwsValidationException() throws ServiceException {
        CourseSectionSnapshot conflictingSection = new CourseSectionSnapshot();
        conflictingSection.setSectionId(200L);
        conflictingSection.setCourseCode("MATH101");
        conflictingSection.setScheduleDays("MWF");
        conflictingSection.setStartTime(Time.valueOf("09:30:00"));
        conflictingSection.setEndTime(Time.valueOf("10:30:00"));

        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "ENROLLED")).thenReturn(List.of(200L));
        when(courseServiceClient.getSectionSnapshots(List.of(200L))).thenReturn(List.of(conflictingSection));
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "COMPLETED")).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> enrollmentFacade.enroll(1L, 100L));
    }

    @Test
    void enroll_transactionFailure_throwsServiceException() throws ServiceException {
        when(enrollmentRepository.findByStudentIdAndSectionId(1L, 100L)).thenReturn(Optional.empty());
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "ENROLLED")).thenReturn(List.of());
        when(enrollmentRepository.findSectionIdsByStudentIdAndStatus(1L, "COMPLETED")).thenReturn(List.of());
        doThrow(new ServiceException("Remote seat reservation failed", "COMMUNICATION_ERROR", 500))
                .when(courseServiceClient).reserveSeat(100L);

        assertThrows(ServiceException.class, () -> enrollmentFacade.enroll(1L, 100L));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void drop_successful_and_promotesWaitlistStudent() throws ServiceException {
        Enrollment activeEnrollment = new Enrollment(1L, 100L, java.time.LocalDate.now(), "ENROLLED");
        activeEnrollment.setId(10L);

        WaitlistEntry waitlistEntry = new WaitlistEntry(2L, 100L, 1, "WAITING");
        waitlistEntry.setId(50L);

        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(activeEnrollment));
        when(courseServiceClient.getSectionSnapshot(100L)).thenReturn(testSection);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));
        when(waitlistRepository.findFirstBySectionIdAndStatusOrderByPositionAsc(100L, "WAITING"))
                .thenReturn(Optional.of(waitlistEntry));

        Enrollment dropped = enrollmentFacade.drop(10L);

        assertEquals("DROPPED", dropped.getStatus());
        verify(courseServiceClient).releaseSeat(100L);
        verify(courseServiceClient).reserveSeat(100L); // For promoted student
        verify(notificationClient).publishEnrollmentDropped(any(), any());
        verify(notificationClient).publishWaitlistSeatAvailable(any(), any());
    }
}
