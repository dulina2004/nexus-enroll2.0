package com.nexusenroll.notification.event;

import lombok.Getter;

@Getter
public class WaitlistAddedEvent extends NotificationEvent {

    private final Long waitlistId;

    public WaitlistAddedEvent(Long studentUserId, String recipientEmail, Long waitlistId, String courseCode, int position) {
        super("WAITLIST_AVAILABLE", studentUserId, null, recipientEmail,
                "Added to Waitlist", "You have been added to waitlist for " + courseCode + " at position " + position,
                "WAITLIST", "WAITLIST", waitlistId, "MEDIUM");
        this.waitlistId = waitlistId;
    }
}
