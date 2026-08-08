package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeStateTest {

    private GradeContext context;

    @BeforeEach
    void setUp() {
        context = new GradeContext(1L, 1001L, 101L, 1L, "Final Exam", 85.0, 100.0, "A", "Good work", "FACULTY", "DRAFT");
    }

    @Test
    @DisplayName("Valid Transition: Draft -> Pending -> Approved")
    void testStateTransitions_DraftToPendingToApproved() {
        assertEquals("DRAFT", context.getStatusName());
        assertTrue(context.canEdit());

        // Submit: DRAFT -> PENDING
        context.submit();
        assertEquals("PENDING", context.getStatusName());
        assertFalse(context.canEdit());

        // Approve: PENDING -> APPROVED
        context.approve();
        assertEquals("APPROVED", context.getStatusName());
        assertFalse(context.canEdit());
    }

    @Test
    @DisplayName("Valid Transition: Pending -> Draft on Reject")
    void testStateTransitions_PendingToDraftOnReject() {
        context.submit();
        assertEquals("PENDING", context.getStatusName());

        // Reject: PENDING -> DRAFT
        context.reject();
        assertEquals("DRAFT", context.getStatusName());
        assertTrue(context.canEdit());
    }

    @Test
    @DisplayName("Invalid Transition: Draft -> Approved throws ValidationException")
    void testInvalidTransition_DraftToApproved_ThrowsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> context.approve());
        assertEquals("Grade must be submitted to Pending state before approval", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Transition: Draft -> Reject throws ValidationException")
    void testInvalidTransition_DraftToReject_ThrowsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> context.reject());
        assertEquals("Grade in Draft state cannot be rejected", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Transition: Pending -> Submit throws ValidationException")
    void testInvalidTransition_PendingToSubmit_ThrowsException() {
        context.submit();
        ValidationException ex = assertThrows(ValidationException.class, () -> context.submit());
        assertEquals("Grade is already submitted and pending approval", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Transition: Approved -> Submit throws ValidationException")
    void testInvalidTransition_ApprovedToSubmit_ThrowsException() {
        context.submit();
        context.approve();
        ValidationException ex = assertThrows(ValidationException.class, () -> context.submit());
        assertEquals("Grade is already approved and locked", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Transition: Approved -> Approve throws ValidationException")
    void testInvalidTransition_ApprovedToApprove_ThrowsException() {
        context.submit();
        context.approve();
        ValidationException ex = assertThrows(ValidationException.class, () -> context.approve());
        assertEquals("Grade is already approved", ex.getMessage());
    }

    @Test
    @DisplayName("Invalid Transition: Approved -> Reject throws ValidationException")
    void testInvalidTransition_ApprovedToReject_ThrowsException() {
        context.submit();
        context.approve();
        ValidationException ex = assertThrows(ValidationException.class, () -> context.reject());
        assertEquals("Approved grade cannot be rejected", ex.getMessage());
    }

    @Test
    @DisplayName("Validation: Negative points earned on Draft submit throws ValidationException")
    void testValidation_NegativePointsEarned_ThrowsException() {
        context.setPointsEarned(-10.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> context.submit());
        assertEquals("Points earned cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Validation: Zero max points on Draft submit throws ValidationException")
    void testValidation_ZeroMaxPoints_ThrowsException() {
        context.setMaxPoints(0.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> context.submit());
        assertEquals("Max points must be greater than zero", ex.getMessage());
    }
}
