package com.nexusenroll.notification.observer;

import com.nexusenroll.notification.model.EmailLog;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer handling email notification generation and persistence.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotifier implements NotificationObserver {

    private final EmailLogRepository emailLogRepository;

    @Override
    public void update(NotificationSubject subject) {
        if (subject.getRecipientEmail() == null || subject.getRecipientEmail().isBlank()) {
            return;
        }

        EmailLog emailLog = EmailLog.builder()
                .recipientEmail(subject.getRecipientEmail())
                .recipientUserId(subject.getRecipientUserId())
                .subject(subject.getTitle())
                .body(subject.getMessage())
                .emailType(subject.getNotificationType())
                .status("SENT")
                .build();

        emailLogRepository.save(emailLog);
        log.info("Email notification logged for email={}: {}", subject.getRecipientEmail(), subject.getTitle());
    }
}
