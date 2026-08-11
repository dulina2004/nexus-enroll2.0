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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Orchestrates notification requests: validates input, fills in default title/message text per
 * event type, and publishes a {@link NotificationEvent} that
 * {@link com.nexusenroll.notification.event.NotificationEventListener} picks up to notify the
 * registered {@link NotificationObserver}s. Also exposes read and mark-as-read operations over
 * persisted {@link Notification} records.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final List<NotificationObserver> observers;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Validates the request, defaults the title/message when omitted, and publishes a
     * {@link NotificationEvent} so {@link com.nexusenroll.notification.event.NotificationEventListener}
     * notifies the registered observers. The {@link NotificationSubject} returned here is a
     * separate instance built for the caller's immediate use; it is never notified directly.
     */
    @Transactional
    public NotificationSubject send(NotificationRequestDto request) {
        validate(request);

        String title = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle()
                : defaultTitle(request.getEventType());

        String message = (request.getMessage() != null && !request.getMessage().isBlank())
                ? request.getMessage()
                : defaultMessage(request.getEventType());

        String notificationType = request.getNotificationType() != null
                ? request.getNotificationType()
                : request.getEventType();

        String priority = request.getPriority() != null ? request.getPriority() : "MEDIUM";

        // Publish Spring Application Event
        NotificationEvent event = new NotificationEvent(
                request.getEventType(),
                request.getRecipientUserId(),
                request.getAdvisorUserId(),
                request.getRecipientEmail(),
                title,
                message,
                notificationType,
                request.getRelatedEntityType(),
                request.getRelatedEntityId(),
                priority
        );
        eventPublisher.publishEvent(event);

        // Also construct and return NotificationSubject for direct Observer pattern inspection
        NotificationSubject subject = new NotificationSubject(
                observers,
                request.getEventType(),
                request.getRecipientUserId(),
                request.getAdvisorUserId(),
                request.getRecipientEmail(),
                title,
                message,
                notificationType,
                request.getRelatedEntityType(),
                request.getRelatedEntityId(),
                priority
        );

        return subject;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsForUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Valid recipient user ID is required");
        }
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotificationsForUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Valid recipient user ID is required");
        }
        return notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnreadCountDto getUnreadCount(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Valid recipient user ID is required");
        }
        long count = notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
        return new UnreadCountDto(userId, count);
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId) {
        if (notificationId == null || notificationId <= 0) {
            throw new ValidationException("Valid notification ID is required");
        }
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToResponseDto(saved);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Valid recipient user ID is required");
        }
        List<Notification> unreadList = notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification n : unreadList) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }

    private void validate(NotificationRequestDto request) {
        if (request == null) {
            throw new ValidationException("Notification request is required");
        }
        if (request.getEventType() == null || request.getEventType().isBlank()) {
            throw new ValidationException("Event type is required");
        }
        if (request.getRecipientUserId() == null || request.getRecipientUserId() <= 0) {
            throw new ValidationException("Recipient user ID is required");
        }
    }

    private String defaultTitle(String eventType) {
        return switch (eventType.toUpperCase()) {
            case "ENROLLMENT_SUCCESS" -> "Enrollment successful";
            case "COURSE_DROPPED" -> "Course dropped";
            case "WAITLIST_AVAILABLE" -> "Waitlist spot available";
            case "GRADE_APPROVED" -> "Grade approved";
            default -> "Notification";
        };
    }

    private String defaultMessage(String eventType) {
        return switch (eventType.toUpperCase()) {
            case "ENROLLMENT_SUCCESS" -> "Your enrollment has been confirmed.";
            case "COURSE_DROPPED" -> "Your course drop request was processed.";
            case "WAITLIST_AVAILABLE" -> "A waitlist spot is now available.";
            case "GRADE_APPROVED" -> "Your course grade has been approved.";
            default -> "You have a new notification.";
        };
    }

    private NotificationResponseDto mapToResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .recipientUserId(notification.getRecipientUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .priority(notification.getPriority())
                .relatedEntityType(notification.getRelatedEntityType())
                .relatedEntityId(notification.getRelatedEntityId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
