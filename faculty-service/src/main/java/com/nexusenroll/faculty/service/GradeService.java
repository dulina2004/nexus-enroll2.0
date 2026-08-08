package com.nexusenroll.faculty.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.faculty.dto.BatchGradeResultDto;
import com.nexusenroll.faculty.model.Grade;
import com.nexusenroll.faculty.repository.GradeRepository;
import com.nexusenroll.faculty.state.GradeContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeService {

    private static final Set<String> ALLOWED_GRADES = Set.of(
            "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F", "I", "W"
    );

    private final GradeRepository gradeRepository;
    private final AcademicRecordPublisher academicRecordPublisher;

    @Transactional
    public GradeContext createOrUpdateDraft(GradeContext grade) {
        if (grade == null || grade.getEnrollmentId() <= 0) {
            throw new ValidationException("Enrollment ID is required for grade creation");
        }

        if (grade.getId() != null && grade.getId() > 0) {
            Grade existingEntity = gradeRepository.findById(grade.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + grade.getId()));

            GradeContext existingContext = mapToContext(existingEntity);
            if (!existingContext.canEdit()) {
                throw new ValidationException("Grade in status '" + existingContext.getStatusName() + "' cannot be edited");
            }
        }

        Grade entity = mapToEntity(grade);
        Grade saved = gradeRepository.save(entity);
        return mapToContext(saved);
    }

    @Transactional
    public GradeContext submitGrade(long gradeId) {
        Grade entity = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));

        GradeContext context = mapToContext(entity);

        // Apply State Pattern transition: Draft -> Pending
        context.submit();

        updateEntityFromContext(entity, context);
        Grade saved = gradeRepository.save(entity);
        return mapToContext(saved);
    }

    @Transactional
    public GradeContext approveGrade(long gradeId) {
        Grade entity = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));

        GradeContext context = mapToContext(entity);

        // Apply State Pattern transition: Pending -> Approved
        context.approve();

        updateEntityFromContext(entity, context);
        Grade saved = gradeRepository.save(entity);

        // Trigger notification to Academic Record Service upon approval
        academicRecordPublisher.notifyGradeApproved(context);

        return mapToContext(saved);
    }

    @Transactional
    public GradeContext rejectGrade(long gradeId) {
        Grade entity = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));

        GradeContext context = mapToContext(entity);

        // Apply State Pattern transition: Pending -> Draft
        context.reject();

        updateEntityFromContext(entity, context);
        Grade saved = gradeRepository.save(entity);
        return mapToContext(saved);
    }

    @Transactional(readOnly = true)
    public GradeContext getGrade(long gradeId) {
        Grade entity = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));
        return mapToContext(entity);
    }

    @Transactional(readOnly = true)
    public List<GradeContext> getGradesByEnrollment(long enrollmentId) {
        return gradeRepository.findByEnrollmentId(enrollmentId).stream()
                .map(this::mapToContext)
                .collect(Collectors.toList());
    }

    /**
     * Submits a batch of grades. Each entry is validated and transitioned independently:
     * an invalid entry is recorded as a failure and skipped, and every valid entry is
     * still moved Draft -> Pending. The batch never rolls back as a whole.
     */
    @Transactional
    public BatchGradeResultDto submitBatch(long sectionId, List<GradeContext> grades) {
        BatchGradeResultDto result = BatchGradeResultDto.builder()
                .submittedGradeIds(new ArrayList<>())
                .failures(new ArrayList<>())
                .build();

        if (grades == null || grades.isEmpty()) {
            return result;
        }

        result.setTotalSubmitted(grades.size());

        for (GradeContext grade : grades) {
            long enrollmentId = grade.getEnrollmentId();
            String submittedGrade = grade.getLetterGrade();

            // 1. Validate grade format
            if (submittedGrade == null || !ALLOWED_GRADES.contains(submittedGrade.trim().toUpperCase())) {
                result.getFailures().add(BatchGradeResultDto.FailedGradeDto.builder()
                        .enrollmentId(enrollmentId)
                        .submittedValue(submittedGrade)
                        .reason("Invalid grade format: '" + submittedGrade + "'")
                        .build());
                continue;
            }
            grade.setLetterGrade(submittedGrade.trim().toUpperCase());

            // 2. Validate enrollment ID
            if (enrollmentId <= 0) {
                result.getFailures().add(BatchGradeResultDto.FailedGradeDto.builder()
                        .enrollmentId(enrollmentId)
                        .submittedValue(submittedGrade)
                        .reason("Invalid enrollment ID: " + enrollmentId)
                        .build());
                continue;
            }

            // 3. Process valid grade through State Pattern transition (Draft -> Pending)
            try {
                grade.setSectionId(sectionId);
                grade.submit(); // State Pattern transition

                Grade entity = mapToEntity(grade);
                Grade saved = gradeRepository.save(entity);

                result.getSubmittedGradeIds().add(saved.getId());
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.getFailures().add(BatchGradeResultDto.FailedGradeDto.builder()
                        .enrollmentId(enrollmentId)
                        .submittedValue(submittedGrade)
                        .reason(e.getMessage())
                        .build());
            }
        }

        result.setFailureCount(result.getFailures().size());
        return result;
    }

    public GradeContext mapToContext(Grade entity) {
        return new GradeContext(
                entity.getId(),
                entity.getEnrollmentId() != null ? entity.getEnrollmentId() : 0L,
                entity.getStudentId() != null ? entity.getStudentId() : 0L,
                entity.getSectionId() != null ? entity.getSectionId() : 0L,
                entity.getAssignmentTitle(),
                entity.getPointsEarned() != null ? entity.getPointsEarned() : 0.0,
                entity.getMaxPoints() != null ? entity.getMaxPoints() : 100.0,
                entity.getLetterGrade(),
                entity.getComments(),
                entity.getGradedBy(),
                entity.getStatus()
        );
    }

    public Grade mapToEntity(GradeContext context) {
        return Grade.builder()
                .id(context.getId())
                .enrollmentId(context.getEnrollmentId())
                .studentId(context.getStudentId())
                .sectionId(context.getSectionId())
                .assignmentTitle(context.getAssignmentTitle())
                .pointsEarned(context.getPointsEarned())
                .maxPoints(context.getMaxPoints())
                .letterGrade(context.getLetterGrade())
                .comments(context.getComments())
                .gradedBy(context.getGradedBy())
                .status(context.getStatusName())
                .build();
    }

    private void updateEntityFromContext(Grade entity, GradeContext context) {
        entity.setAssignmentTitle(context.getAssignmentTitle());
        entity.setPointsEarned(context.getPointsEarned());
        entity.setMaxPoints(context.getMaxPoints());
        entity.setLetterGrade(context.getLetterGrade());
        entity.setComments(context.getComments());
        entity.setGradedBy(context.getGradedBy());
        entity.setStatus(context.getStatusName());
    }
}
