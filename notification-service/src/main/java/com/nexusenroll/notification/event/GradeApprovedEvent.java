package com.nexusenroll.notification.event;

import lombok.Getter;

/** Fired when a student's course grade is approved; carries the grade ID for the {@code GRADE_APPROVED} notification. */
@Getter
public class GradeApprovedEvent extends NotificationEvent {

    private final Long gradeId;

    public GradeApprovedEvent(Long studentUserId, String recipientEmail, Long gradeId, String courseCode, String letterGrade) {
        super("GRADE_APPROVED", studentUserId, null, recipientEmail,
                "Grade Approved", "Your grade for " + courseCode + " has been approved: " + letterGrade,
                "GRADE", "GRADE", gradeId, "HIGH");
        this.gradeId = gradeId;
    }
}
