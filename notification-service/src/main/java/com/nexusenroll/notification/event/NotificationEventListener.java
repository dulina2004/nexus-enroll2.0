package com.nexusenroll.notification.event;

import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.observer.NotificationObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Application Event Listener that Bridges Spring Events to the Observer Pattern.
 * Loose-couples event publishers from concrete observer handling.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final List<NotificationObserver> observers;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received Spring NotificationEvent: eventType={}, recipientUserId={}",
                event.getEventType(), event.getRecipientUserId());

        NotificationSubject subject = new NotificationSubject(
                observers,
                event.getEventType(),
                event.getRecipientUserId(),
                event.getAdvisorUserId(),
                event.getRecipientEmail(),
                event.getTitle(),
                event.getMessage(),
                event.getNotificationType(),
                event.getRelatedEntityType(),
                event.getRelatedEntityId(),
                event.getPriority()
        );

        subject.notifyObservers();
    }
}
