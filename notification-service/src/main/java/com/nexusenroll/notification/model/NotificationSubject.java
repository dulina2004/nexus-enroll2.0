package com.nexusenroll.notification.model;

import com.nexusenroll.notification.observer.NotificationObserver;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Subject role in the Observer Design Pattern.
 * Holds notification event data and notifies all registered observers upon update.
 */
@Getter
public class NotificationSubject {

    private final List<NotificationObserver> observers;
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

    public NotificationSubject(List<NotificationObserver> observers,
                               String eventType,
                               Long recipientUserId,
                               Long advisorUserId,
                               String recipientEmail,
                               String title,
                               String message,
                               String notificationType,
                               String relatedEntityType,
                               Long relatedEntityId,
                               String priority) {
        this.observers = new ArrayList<>(observers != null ? observers : Collections.emptyList());
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

    public void attach(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (NotificationObserver observer : observers) {
            observer.update(this);
        }
    }

    public List<NotificationObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }
}
