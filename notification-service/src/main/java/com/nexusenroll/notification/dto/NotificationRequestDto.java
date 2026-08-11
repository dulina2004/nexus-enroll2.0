package com.nexusenroll.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload describing the event to notify a recipient about, submitted to {@link com.nexusenroll.notification.controller.NotificationController}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Recipient user ID is required")
    private Long recipientUserId;

    private Long advisorUserId;
    private String recipientEmail;
    private String title;
    private String message;
    private String notificationType;
    private String relatedEntityType;
    private Long relatedEntityId;
    private String priority;
}
