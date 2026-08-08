package com.nexusenroll.notification.observer;

import com.nexusenroll.notification.model.EmailLog;
import com.nexusenroll.notification.model.Notification;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.repository.EmailLogRepository;
import com.nexusenroll.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationObserverTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailLogRepository emailLogRepository;

    @InjectMocks
    private StudentNotifier studentNotifier;

    @InjectMocks
    private AdvisorNotifier advisorNotifier;

    @InjectMocks
    private EmailNotifier emailNotifier;

    @Test
    @DisplayName("StudentNotifier creates in-app notification when student user id is provided")
    void testStudentNotifier() {
        NotificationSubject subject = new NotificationSubject(
                List.of(studentNotifier),
                "ENROLLMENT_SUCCESS",
                101L,
                null,
                "student@univ.edu",
                "Enrollment Confirmed",
                "Successfully enrolled in CS101",
                "ENROLLMENT",
                "ENROLLMENT",
                1L,
                "HIGH"
        );

        subject.notifyObservers();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(101L, saved.getRecipientUserId());
        assertEquals("Enrollment Confirmed", saved.getTitle());
    }

    @Test
    @DisplayName("AdvisorNotifier creates in-app notification when advisor user id is provided")
    void testAdvisorNotifier() {
        NotificationSubject subject = new NotificationSubject(
                List.of(advisorNotifier),
                "ENROLLMENT_SUCCESS",
                101L,
                501L,
                "student@univ.edu",
                "Enrollment Confirmed",
                "Successfully enrolled in CS101",
                "ENROLLMENT",
                "ENROLLMENT",
                1L,
                "HIGH"
        );

        subject.notifyObservers();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(501L, saved.getRecipientUserId());
        assertEquals("[Advisor Alert] Enrollment Confirmed", saved.getTitle());
    }

    @Test
    @DisplayName("AdvisorNotifier ignores event when advisor user id is null")
    void testAdvisorNotifierNullAdvisor() {
        NotificationSubject subject = new NotificationSubject(
                List.of(advisorNotifier),
                "ENROLLMENT_SUCCESS",
                101L,
                null,
                "student@univ.edu",
                "Enrollment Confirmed",
                "Successfully enrolled in CS101",
                "ENROLLMENT",
                "ENROLLMENT",
                1L,
                "HIGH"
        );

        subject.notifyObservers();

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("EmailNotifier logs email entry when recipient email is provided")
    void testEmailNotifier() {
        NotificationSubject subject = new NotificationSubject(
                List.of(emailNotifier),
                "ENROLLMENT_SUCCESS",
                101L,
                null,
                "student@univ.edu",
                "Enrollment Confirmed",
                "Successfully enrolled in CS101",
                "ENROLLMENT",
                "ENROLLMENT",
                1L,
                "HIGH"
        );

        subject.notifyObservers();

        ArgumentCaptor<EmailLog> captor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository).save(captor.capture());

        EmailLog saved = captor.getValue();
        assertEquals("student@univ.edu", saved.getRecipientEmail());
        assertEquals("Enrollment Confirmed", saved.getSubject());
        assertEquals("SENT", saved.getStatus());
    }
}
