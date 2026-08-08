package com.nexusenroll.notification.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.notification.dto.NotificationRequestDto;
import com.nexusenroll.notification.dto.NotificationResponseDto;
import com.nexusenroll.notification.dto.UnreadCountDto;
import com.nexusenroll.notification.event.NotificationEvent;
import com.nexusenroll.notification.model.Notification;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.observer.NotificationObserver;
import com.nexusenroll.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificationObserver observer;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("send publishes NotificationEvent and returns NotificationSubject")
    void testSend() {
        NotificationRequestDto dto = NotificationRequestDto.builder()
                .eventType("ENROLLMENT_SUCCESS")
                .recipientUserId(101L)
                .recipientEmail("student@univ.edu")
                .title("Enrollment Confirmed")
                .message("You are enrolled in CS101")
                .build();

        NotificationSubject subject = notificationService.send(dto);

        assertNotNull(subject);
        assertEquals("ENROLLMENT_SUCCESS", subject.getEventType());
        assertEquals(101L, subject.getRecipientUserId());

        verify(eventPublisher).publishEvent(any(NotificationEvent.class));
    }

    @Test
    @DisplayName("send throws ValidationException when required fields are missing")
    void testSendValidationException() {
        NotificationRequestDto dto = NotificationRequestDto.builder().build();
        assertThrows(ValidationException.class, () -> notificationService.send(dto));
    }

    @Test
    @DisplayName("markAsRead updates notification isRead status to true")
    void testMarkAsRead() {
        Notification n = Notification.builder()
                .id(1L)
                .recipientUserId(101L)
                .isRead(false)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponseDto result = notificationService.markAsRead(1L);

        assertNotNull(result);
        assertTrue(result.isRead());
    }

    @Test
    @DisplayName("markAsRead throws ResourceNotFoundException for unknown ID")
    void testMarkAsReadNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(999L));
    }

    @Test
    @DisplayName("getUnreadCount returns correct count for user")
    void testGetUnreadCount() {
        when(notificationRepository.countByRecipientUserIdAndIsReadFalse(101L)).thenReturn(5L);

        UnreadCountDto dto = notificationService.getUnreadCount(101L);

        assertNotNull(dto);
        assertEquals(101L, dto.getRecipientUserId());
        assertEquals(5L, dto.getUnreadCount());
    }
}
