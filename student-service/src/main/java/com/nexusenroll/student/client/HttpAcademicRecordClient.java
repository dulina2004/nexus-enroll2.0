package com.nexusenroll.student.client;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.student.client.dto.RemoteAcademicRecordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
@Slf4j
public class HttpAcademicRecordClient implements AcademicRecordClient {

    private final RestClient restClient;

    public HttpAcademicRecordClient(RestClient.Builder restClientBuilder,
                                    @Value("${services.academic-record-service.url:http://localhost:8086}") String recordServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(recordServiceUrl).build();
    }

    @Override
    public Optional<RemoteAcademicRecordDto> getAcademicRecord(long studentId) {
        try {
            ApiResponse<RemoteAcademicRecordDto> response = restClient.get()
                    .uri("/records?studentId={studentId}", studentId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<RemoteAcademicRecordDto>>() {});

            if (response != null && response.getData() != null) {
                return Optional.of(response.getData());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Academic record for student ID {} not found in Academic Record Service (404)", studentId);
        } catch (Exception e) {
            log.error("Failed to fetch academic record from Academic Record Service: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
