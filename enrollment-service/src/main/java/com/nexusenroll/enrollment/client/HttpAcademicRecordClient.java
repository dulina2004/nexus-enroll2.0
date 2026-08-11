package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.enrollment.client.dto.RemoteCompletedCourseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * REST-based {@link AcademicRecordClient} that calls the Academic Record Service over
 * HTTP to fetch a student's completed courses, returning an empty list if the service
 * is unreachable or no record is found.
 */
@Component
@Slf4j
public class HttpAcademicRecordClient implements AcademicRecordClient {

    private final RestClient restClient;

    public HttpAcademicRecordClient(RestClient.Builder restClientBuilder,
                                    @Value("${services.academic-record-service.url:http://localhost:8086}") String recordServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(recordServiceUrl).build();
    }

    @Override
    public List<RemoteCompletedCourseDto> getCompletedCourses(long studentId) {
        try {
            ApiResponse<List<RemoteCompletedCourseDto>> response = restClient.get()
                    .uri("/records/completed-courses?studentId={studentId}", studentId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<RemoteCompletedCourseDto>>>() {});

            if (response != null && response.getData() != null) {
                return response.getData();
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Completed courses for student ID {} not found in Academic Record Service (404)", studentId);
        } catch (Exception e) {
            log.error("Failed to fetch completed courses from Academic Record Service: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
