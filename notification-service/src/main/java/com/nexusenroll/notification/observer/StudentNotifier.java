package com.nexusenroll.notification.observer;

import com.nexusenroll.notification.model.Notification;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer handling student in-app notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentNotifier implements NotificationObserver {

    private final NotificationRepository notificationRepository;

    @Override
    public void update(NotificationSubject subject) {
        if (subject.getRecipientUserId() == null || subject.getRecipientUserId() <= 0) {
            return;
        }
        if (!isStudentEvent(subject.getEventType())) {
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(subject.getRecipientUserId())
                .title(subject.getTitle())
                .message(subject.getMessage())
                .notificationType(subject.getNotificationType())
                .priority(subject.getPriority())
                .relatedEntityType(subject.getRelatedEntityType())
                .relatedEntityId(subject.getRelatedEntityId())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Student notification saved for userId={}: {}", subject.getRecipientUserId(), subject.getTitle());
    }

    private boolean isStudentEvent(String eventType) {
        if (eventType == null) {
            return true;
        }
        String upper = eventType.toUpperCase();
        return upper.contains("ENROLLMENT") || upper.contains("COURSE") || upper.contains("WAITLIST") || upper.contains("GRADE") || upper.contains("STUDENT");
    }
}
