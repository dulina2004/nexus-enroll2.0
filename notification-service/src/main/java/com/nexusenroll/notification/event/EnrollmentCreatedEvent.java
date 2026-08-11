package com.nexusenroll.notification.event;

import lombok.Getter;

/** Fired when a student successfully enrolls in a course; carries the enrollment ID for the {@code ENROLLMENT_SUCCESS} notification. */
@Getter
public class EnrollmentCreatedEvent extends NotificationEvent {

    private final Long enrollmentId;

    public EnrollmentCreatedEvent(Long studentUserId, Long advisorUserId, String recipientEmail,
                                  Long enrollmentId, String courseCode) {
        super("ENROLLMENT_SUCCESS", studentUserId, advisorUserId, recipientEmail,
                "Enrollment Confirmed", "Successfully enrolled in course: " + courseCode,
                "ENROLLMENT", "ENROLLMENT", enrollmentId, "HIGH");
        this.enrollmentId = enrollmentId;
    }
}
