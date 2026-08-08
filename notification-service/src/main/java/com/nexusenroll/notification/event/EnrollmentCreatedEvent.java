package com.nexusenroll.notification.event;

import lombok.Getter;

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
