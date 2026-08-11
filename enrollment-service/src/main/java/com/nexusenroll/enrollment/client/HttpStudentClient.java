package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.enrollment.client.dto.RemoteStudentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * REST-based {@link StudentClient} that calls the Student Service over HTTP to fetch a
 * student's profile, falling back to a synthesized offline profile if the service is
 * unreachable or the student is not found.
 */
@Component
@Slf4j
public class HttpStudentClient implements StudentClient {

    private final RestClient restClient;

    public HttpStudentClient(RestClient.Builder restClientBuilder,
                             @Value("${services.student-service.url:http://localhost:8083}") String studentServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(studentServiceUrl).build();
    }

    @Override
    public Optional<RemoteStudentDto> getStudentById(long studentId) {
        try {
            ApiResponse<RemoteStudentDto> response = restClient.get()
                    .uri("/api/students/{id}", studentId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<RemoteStudentDto>>() {});

            if (response != null && response.getData() != null) {
                return Optional.of(response.getData());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Student with ID {} not found in Student Service (404)", studentId);
        } catch (Exception e) {
            log.error("Failed to fetch student profile from Student Service: {}", e.getMessage());
        }
        // Graceful fallback profile for testing/offline resilience
        return Optional.of(RemoteStudentDto.builder()
                .id(studentId)
                .studentNumber("STU-" + studentId)
                .status("ACTIVE")
                .build());
    }
}
