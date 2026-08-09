package com.nexusenroll.notification.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.notification.dto.NotificationRequestDto;
import com.nexusenroll.notification.dto.NotificationResponseDto;
import com.nexusenroll.notification.dto.UnreadCountDto;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponseDto>> sendNotification(
            @Valid @RequestBody NotificationRequestDto dto) {

        NotificationSubject subject = notificationService.send(dto);
        NotificationResponseDto response = NotificationResponseDto.builder()
                .recipientUserId(subject.getRecipientUserId())
                .title(subject.getTitle())
                .message(subject.getMessage())
                .notificationType(subject.getNotificationType())
                .priority(subject.getPriority())
                .relatedEntityType(subject.getRelatedEntityType())
                .relatedEntityId(subject.getRelatedEntityId())
                .isRead(false)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Notification dispatched successfully", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getNotificationsForUser(
            @PathVariable("userId") Long userId) {

        List<NotificationResponseDto> list = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", list));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUnreadNotificationsForUser(
            @PathVariable("userId") Long userId) {

        List<NotificationResponseDto> list = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved successfully", list));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountDto>> getUnreadCount(
            @PathVariable("userId") Long userId) {

        UnreadCountDto countDto = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", countDto));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markAsRead(
            @PathVariable("id") Long id) {

        NotificationResponseDto response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @PathVariable("userId") Long userId) {

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", "SUCCESS"));
    }
}
