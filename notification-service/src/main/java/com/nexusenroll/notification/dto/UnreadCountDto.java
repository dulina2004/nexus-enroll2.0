package com.nexusenroll.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload carrying a user's unread notification count. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadCountDto {

    private Long recipientUserId;
    private long unreadCount;
}
