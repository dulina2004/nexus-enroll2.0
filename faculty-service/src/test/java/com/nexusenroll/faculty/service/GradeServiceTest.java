package com.nexusenroll.faculty.service;

import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.faculty.dto.BatchGradeResultDto;
import com.nexusenroll.faculty.model.Grade;
import com.nexusenroll.faculty.repository.GradeRepository;
import com.nexusenroll.faculty.state.GradeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private AcademicRecordPublisher academicRecordPublisher;

    @InjectMocks
    private GradeService gradeService;

    private Grade draftGrade;

    @BeforeEach
    void setUp() {
        draftGrade = Grade.builder()
                .id(1L)
                .enrollmentId(101L)
                .studentId(201L)
                .sectionId(10L)
                .assignmentTitle("Midterm")
                .pointsEarned(88.0)
                .maxPoints(100.0)
                .letterGrade("B+")
                .comments("Good effort")
                .gradedBy("FACULTY")
                .status("DRAFT")
                .build();
    }

    @Test
    @DisplayName("submitGrade transitions state from DRAFT to PENDING")
    void testSubmitGrade_Success() {
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(draftGrade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradeContext result = gradeService.submitGrade(1L);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatusName());
        verify(gradeRepository).save(draftGrade);
    }

    @Test
    @DisplayName("approveGrade transitions state from PENDING to APPROVED and notifies academic publisher")
    void testApproveGrade_Success() {
        draftGrade.setStatus("PENDING");
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(draftGrade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradeContext result = gradeService.approveGrade(1L);

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatusName());
        verify(academicRecordPublisher).notifyGradeApproved(any(GradeContext.class));
    }

    @Test
    @DisplayName("rejectGrade transitions state from PENDING back to DRAFT")
    void testRejectGrade_Success() {
        draftGrade.setStatus("PENDING");
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(draftGrade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradeContext result = gradeService.rejectGrade(1L);

        assertNotNull(result);
        assertEquals("DRAFT", result.getStatusName());
    }

    @Test
    @DisplayName("Editing an APPROVED grade throws ValidationException")
    void testUpdateDraft_ApprovedGrade_ThrowsException() {
        draftGrade.setStatus("APPROVED");
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(draftGrade));

        GradeContext updateContext = new GradeContext(1L, 101L, 201L, 10L, "Midterm", 95.0, 100.0, "A", "Updated", "FACULTY", "DRAFT");

        assertThrows(ValidationException.class, () -> gradeService.createOrUpdateDraft(updateContext));
    }

    @Test
    @DisplayName("submitBatch processes valid entries and logs invalid entries")
    void testSubmitBatch() {
        GradeContext g1 = new GradeContext(null, 101L, 201L, 10L, "Final", 90.0, 100.0, "A", null, "FACULTY", "DRAFT");
        GradeContext g2 = new GradeContext(null, 102L, 202L, 10L, "Final", 70.0, 100.0, "INVALID_GRADE", null, "FACULTY", "DRAFT");

        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> {
            Grade g = invocation.getArgument(0);
            g.setId(10L);
            return g;
        });

        BatchGradeResultDto result = gradeService.submitBatch(10L, List.of(g1, g2));

        assertEquals(2, result.getTotalSubmitted());
        assertEquals(1, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getSubmittedGradeIds().size());
        assertEquals(1, result.getFailures().size());
    }
}
