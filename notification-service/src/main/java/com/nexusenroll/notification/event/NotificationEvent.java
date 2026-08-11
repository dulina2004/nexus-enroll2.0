package com.nexusenroll.notification.event;

import lombok.Getter;

/** Base Spring application event carrying the data needed to notify a recipient; extended by {@link EnrollmentCreatedEvent}, {@link GradeApprovedEvent}, and {@link WaitlistAddedEvent}. */
@Getter
public class NotificationEvent {

    private final String eventType;
    private final Long recipientUserId;
    private final Long advisorUserId;
    private final String recipientEmail;
    private final String title;
    private final String message;
    private final String notificationType;
    private final String relatedEntityType;
    private final Long relatedEntityId;
    private final String priority;

    public NotificationEvent(String eventType, Long recipientUserId, Long advisorUserId,
                             String recipientEmail, String title, String message,
                             String notificationType, String relatedEntityType,
                             Long relatedEntityId, String priority) {
        this.eventType = eventType;
        this.recipientUserId = recipientUserId;
        this.advisorUserId = advisorUserId;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.priority = priority;
    }
}
