package com.nexusenroll.notification.integration;

import com.nexusenroll.notification.event.EnrollmentCreatedEvent;
import com.nexusenroll.notification.model.EmailLog;
import com.nexusenroll.notification.model.Notification;
import com.nexusenroll.notification.repository.EmailLogRepository;
import com.nexusenroll.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        emailLogRepository.deleteAll();
    }

    @Test
    void shouldPublishEventAndTriggerObservers() {
        // Create an event
        EnrollmentCreatedEvent event = new EnrollmentCreatedEvent(
                100L, 200L, "student@nexus.edu", 10L, "CS101"
        );

        // Publish event
        eventPublisher.publishEvent(event);

        // Fetch notifications
        List<Notification> notifications = notificationRepository.findAll();
        List<EmailLog> emails = emailLogRepository.findAll();

        // Observers should have created a notification for the student
        boolean hasStudentNotification = notifications.stream()
                .anyMatch(n -> n.getRecipientUserId() != null && n.getRecipientUserId().equals(100L) && n.getMessage().contains("CS101"));

        assertTrue(hasStudentNotification, "StudentNotifier should create an in-app notification");

        // Observers should have created an email log
        boolean hasEmail = emails.stream()
                .anyMatch(e -> e.getRecipientEmail() != null && e.getRecipientEmail().equals("student@nexus.edu"));

        assertTrue(hasEmail, "EmailNotifier should create an email log");
    }
}
