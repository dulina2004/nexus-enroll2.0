package com.nexusenroll.reporting.client;

import com.nexusenroll.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HttpCourseClient implements CourseClient {

    private final RestClient restClient;

    public HttpCourseClient(RestClient.Builder restClientBuilder,
                            @Value("${services.course-service.url:http://localhost:8082}") String courseServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(courseServiceUrl).build();
    }

    @Override
    public List<Map<String, Object>> getCourses() {
        try {
            ApiResponse<List<Map<String, Object>>> response = restClient.get()
                    .uri("/api/courses")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {});

            if (response != null && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch courses from Course Service (offline fallback active): {}", e.getMessage());
        }
        return List.of(
                Map.of("courseCode", "CS101", "title", "Intro to Computer Science", "studentCount", 120),
                Map.of("courseCode", "MATH201", "title", "Calculus II", "studentCount", 85),
                Map.of("courseCode", "ENG102", "title", "Academic Writing", "studentCount", 60)
        );
    }
}
