package com.nexusenroll.academicrecord.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service API Integration client for communicating with Faculty Service.
 * Avoids direct database access across microservice boundaries.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FacultyServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.faculty-service.url:http://localhost:8005}")
    private String facultyServiceUrl;

    public Map<String, Object> getGradeDetails(Long gradeId) {
        if (gradeId == null || gradeId <= 0) {
            return null;
        }
        try {
            String url = facultyServiceUrl + "/faculty/grades/" + gradeId;
            log.info("Calling Faculty Service API at {}", url);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response;
        } catch (Exception e) {
            log.warn("Faculty Service API call failed for gradeId {}: {}", gradeId, e.getMessage());
            return null;
        }
    }
}
