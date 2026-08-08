package com.nexusenroll.notification.observer;

import com.nexusenroll.notification.model.NotificationSubject;

/**
 * Observer interface in the Observer Design Pattern.
 */
public interface NotificationObserver {

    void update(NotificationSubject subject);
}
