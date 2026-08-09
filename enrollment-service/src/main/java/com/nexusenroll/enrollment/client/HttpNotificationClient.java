package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Primary
@Slf4j
public class HttpNotificationClient implements NotificationClient {

    private final RestClient restClient;

    public HttpNotificationClient(RestClient.Builder restClientBuilder,
                                  @Value("${services.notification-service.url:http://localhost:8087}") String notificationServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(notificationServiceUrl).build();
    }

    @Override
    public void publishEnrollmentCreated(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException {
        sendNotification(
                enrollment.getStudentId(),
                "Enrollment Successful",
                "Successfully enrolled in course section " + (section != null ? section.getSectionId() : enrollment.getSectionId()),
                "ENROLLMENT",
                "HIGH",
                "ENROLLMENT",
                enrollment.getId()
        );
    }

    @Override
    public void publishEnrollmentDropped(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException {
        sendNotification(
                enrollment.getStudentId(),
                "Enrollment Dropped",
                "Dropped enrollment in course section " + (section != null ? section.getSectionId() : enrollment.getSectionId()),
                "ENROLLMENT",
                "MEDIUM",
                "ENROLLMENT",
                enrollment.getId()
        );
    }

    @Override
    public void publishWaitlistJoined(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException {
        sendNotification(
                entry.getStudentId(),
                "Waitlist Joined",
                "Added to waitlist for section " + (section != null ? section.getSectionId() : entry.getSectionId()),
                "WAITLIST",
                "MEDIUM",
                "WAITLIST",
                entry.getId()
        );
    }

    @Override
    public void publishWaitlistSeatAvailable(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException {
        sendNotification(
                entry.getStudentId(),
                "Waitlist Seat Available",
                "A seat is now available for section " + (section != null ? section.getSectionId() : entry.getSectionId()),
                "WAITLIST",
                "HIGH",
                "WAITLIST",
                entry.getId()
        );
    }

    private void sendNotification(Long recipientUserId, String title, String message,
                                  String type, String priority, String relatedType, Long relatedId) {
        try {
            NotificationPayload payload = NotificationPayload.builder()
                    .recipientUserId(recipientUserId)
                    .title(title)
                    .message(message)
                    .notificationType(type)
                    .priority(priority)
                    .relatedEntityType(relatedType)
                    .relatedEntityId(relatedId)
                    .build();

            restClient.post()
                    .uri("/notifications")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Dispatched HTTP notification to user {} - {}", recipientUserId, title);
        } catch (Exception e) {
            log.warn("Failed to dispatch HTTP notification to Notification Service (offline fallback active): {}", e.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class NotificationPayload {
        private Long recipientUserId;
        private String title;
        private String message;
        private String notificationType;
        private String priority;
        private String relatedEntityType;
        private Long relatedEntityId;
    }
}
