package com.nexusenroll.faculty.client;

import com.nexusenroll.faculty.state.GradeContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * HTTP implementation of {@link AcademicRecordClient} that posts an approved grade
 * to the Academic Record Service via {@link RestClient}.
 */
@Component
@Slf4j
public class HttpAcademicRecordClient implements AcademicRecordClient {

    private final RestClient restClient;

    public HttpAcademicRecordClient(RestClient.Builder restClientBuilder,
                                    @Value("${services.academic-record-service.url:http://localhost:8086}") String recordServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(recordServiceUrl).build();
    }

    /**
     * Posts the grade to the Academic Record Service. Any failure (including the
     * service being unreachable) is logged and swallowed rather than propagated,
     * so a downed Academic Record Service never fails grade approval.
     */
    @Override
    public void recordGrade(GradeContext grade) {
        if (grade == null) {
            return;
        }
        try {
            GradeRecordPayload payload = GradeRecordPayload.builder()
                    .enrollmentId(grade.getEnrollmentId())
                    .studentId(grade.getStudentId())
                    .sectionId(grade.getSectionId())
                    .assignmentTitle(grade.getAssignmentTitle())
                    .pointsEarned(grade.getPointsEarned())
                    .maxPoints(grade.getMaxPoints())
                    .letterGrade(grade.getLetterGrade())
                    .comments(grade.getComments())
                    .gradedBy(grade.getGradedBy())
                    .build();

            restClient.post()
                    .uri("/records/grades")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully posted approved grade to Academic Record Service for enrollment ID {}", grade.getEnrollmentId());
        } catch (Exception e) {
            log.warn("Failed to post grade to Academic Record Service (offline fallback active): {}", e.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class GradeRecordPayload {
        private Long enrollmentId;
        private Long studentId;
        private Long sectionId;
        private String assignmentTitle;
        private Double pointsEarned;
        private Double maxPoints;
        private String letterGrade;
        private String comments;
        private String gradedBy;
    }
}
