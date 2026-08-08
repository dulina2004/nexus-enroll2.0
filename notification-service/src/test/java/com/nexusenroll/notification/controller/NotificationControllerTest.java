package com.nexusenroll.notification.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.notification.dto.NotificationResponseDto;
import com.nexusenroll.notification.dto.UnreadCountDto;
import com.nexusenroll.notification.model.NotificationSubject;
import com.nexusenroll.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /notifications returns 201 CREATED")
    void testSendNotification() throws Exception {
        NotificationSubject subject = new NotificationSubject(
                List.of(), "ENROLLMENT_SUCCESS", 101L, null, "student@univ.edu",
                "Enrollment Confirmed", "Enrolled in CS101", "ENROLLMENT", "ENROLLMENT", 1L, "HIGH"
        );

        when(notificationService.send(any())).thenReturn(subject);

        String json = """
                {
                    "eventType": "ENROLLMENT_SUCCESS",
                    "recipientUserId": 101,
                    "recipientEmail": "student@univ.edu",
                    "title": "Enrollment Confirmed",
                    "message": "Enrolled in CS101"
                }
                """;

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.recipientUserId").value(101));
    }

    @Test
    @DisplayName("GET /notifications/user/101 returns 200 OK")
    void testGetNotificationsForUser() throws Exception {
        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(1L)
                .recipientUserId(101L)
                .title("Welcome")
                .message("Welcome to NexusEnroll")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationService.getNotificationsForUser(101L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/notifications/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Welcome"));
    }

    @Test
    @DisplayName("GET /notifications/user/101/unread-count returns 200 OK")
    void testGetUnreadCount() throws Exception {
        UnreadCountDto countDto = new UnreadCountDto(101L, 3L);
        when(notificationService.getUnreadCount(101L)).thenReturn(countDto);

        mockMvc.perform(get("/notifications/user/101/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.unreadCount").value(3));
    }

    @Test
    @DisplayName("POST /notifications/1/read returns 200 OK")
    void testMarkAsRead() throws Exception {
        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(1L)
                .recipientUserId(101L)
                .isRead(true)
                .build();

        when(notificationService.markAsRead(1L)).thenReturn(dto);

        mockMvc.perform(post("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.isRead").value(true));
    }
}
