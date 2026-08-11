package com.nexusenroll.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Response payload representing a notification returned to API clients. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

    private Long id;
    private Long recipientUserId;
    private String title;
    private String message;
    private String notificationType;
    private String priority;
    private String relatedEntityType;
    private Long relatedEntityId;

    @JsonProperty("isRead")
    private boolean isRead;

    private LocalDateTime createdAt;

    @JsonProperty("isRead")
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}
