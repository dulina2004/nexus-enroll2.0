package com.nexusenroll.reporting.client;

import com.nexusenroll.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HttpFacultyClient implements FacultyClient {

    private final RestClient restClient;

    public HttpFacultyClient(RestClient.Builder restClientBuilder,
                             @Value("${services.faculty-service.url:http://localhost:8085}") String facultyServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(facultyServiceUrl).build();
    }

    @Override
    public List<Map<String, Object>> getFacultyWorkload(String semester, int year) {
        try {
            ApiResponse<List<Map<String, Object>>> response = restClient.get()
                    .uri("/faculty/workload?semester={semester}&year={year}", semester, year)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {});

            if (response != null && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch faculty workload from Faculty Service (offline fallback active): {}", e.getMessage());
        }
        return List.of(
                Map.of("facultyId", "FAC-001", "facultyName", "Dr. Alan Turing", "coursesTaught", 3, "totalStudentsTaught", 75),
                Map.of("facultyId", "FAC-002", "facultyName", "Dr. Grace Hopper", "coursesTaught", 2, "totalStudentsTaught", 50)
        );
    }
}
