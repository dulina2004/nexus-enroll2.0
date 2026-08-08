package com.nexusenroll.notification.event;

import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.observer.NotificationObserver;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventTest {

    @Mock
    private NotificationObserver observer;

    @Test
    @DisplayName("NotificationEventListener triggers attached observers when handling NotificationEvent")
    void testEventListener() {
        NotificationEventListener listener = new NotificationEventListener(List.of(observer));

        EnrollmentCreatedEvent event = new EnrollmentCreatedEvent(
                101L, 501L, "student@univ.edu", 10L, "CS101"
        );

        listener.handleNotificationEvent(event);

        ArgumentCaptor<NotificationSubject> captor = ArgumentCaptor.forClass(NotificationSubject.class);
        verify(observer).update(captor.capture());

        NotificationSubject subject = captor.getValue();
        assertNotNull(subject);
        assertEquals("ENROLLMENT_SUCCESS", subject.getEventType());
        assertEquals(101L, subject.getRecipientUserId());
        assertEquals(501L, subject.getAdvisorUserId());
        assertEquals("student@univ.edu", subject.getRecipientEmail());
    }
}
