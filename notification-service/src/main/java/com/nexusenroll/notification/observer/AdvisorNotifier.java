package com.nexusenroll.notification.observer;

import com.nexusenroll.notification.model.Notification;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer handling academic advisor notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdvisorNotifier implements NotificationObserver {

    private final NotificationRepository notificationRepository;

    @Override
    public void update(NotificationSubject subject) {
        if (subject.getAdvisorUserId() == null || subject.getAdvisorUserId() <= 0) {
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(subject.getAdvisorUserId())
                .title("[Advisor Alert] " + subject.getTitle())
                .message("Student Alert: " + subject.getMessage())
                .notificationType(subject.getNotificationType())
                .priority(subject.getPriority())
                .relatedEntityType(subject.getRelatedEntityType())
                .relatedEntityId(subject.getRelatedEntityId())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Advisor notification saved for advisorUserId={}: {}", subject.getAdvisorUserId(), subject.getTitle());
    }
}
