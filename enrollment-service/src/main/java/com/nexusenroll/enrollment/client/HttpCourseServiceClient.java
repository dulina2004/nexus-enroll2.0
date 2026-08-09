package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.client.dto.RemoteCourseSectionDto;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@Slf4j
public class HttpCourseServiceClient implements CourseServiceClient {

    private final RestClient restClient;
    private final DefaultCourseServiceClient fallbackClient;

    public HttpCourseServiceClient(RestClient.Builder restClientBuilder,
                                   DefaultCourseServiceClient fallbackClient,
                                   @Value("${services.course-service.url:http://localhost:8082}") String courseServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(courseServiceUrl).build();
        this.fallbackClient = fallbackClient;
    }

    @Override
    public CourseSectionSnapshot getSectionSnapshot(long sectionId) throws ServiceException {
        try {
            ApiResponse<RemoteCourseSectionDto> response = restClient.get()
                    .uri("/api/courses/sections/{sectionId}", sectionId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<RemoteCourseSectionDto>>() {});

            if (response != null && response.getData() != null) {
                RemoteCourseSectionDto dto = response.getData();
                return mapToSnapshot(dto);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Section {} not found on remote Course Service (404)", sectionId);
            throw new ResourceNotFoundException("Course section not found: " + sectionId);
        } catch (Exception e) {
            log.warn("Failed to reach Course Service for section {}: {}. Falling back to default store.", sectionId, e.getMessage());
        }
        return fallbackClient.getSectionSnapshot(sectionId);
    }

    @Override
    public List<CourseSectionSnapshot> getSectionSnapshots(List<Long> sectionIds) throws ServiceException {
        if (sectionIds == null || sectionIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<CourseSectionSnapshot> result = new ArrayList<>();
        for (Long id : sectionIds) {
            try {
                result.add(getSectionSnapshot(id));
            } catch (ResourceNotFoundException e) {
                log.debug("Section {} skipped: not found", id);
            }
        }
        return result;
    }

    @Override
    public void reserveSeat(long sectionId) throws ServiceException {
        try {
            restClient.post()
                    .uri("/api/courses/sections/{sectionId}/enroll", sectionId)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Reserved seat for section {} via Course Service API", sectionId);
        } catch (Exception e) {
            log.warn("Remote reserveSeat failed for section {}: {}. Executing fallback reserve.", sectionId, e.getMessage());
            fallbackClient.reserveSeat(sectionId);
        }
    }

    @Override
    public void releaseSeat(long sectionId) throws ServiceException {
        try {
            restClient.post()
                    .uri("/api/courses/sections/{sectionId}/drop", sectionId)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Released seat for section {} via Course Service API", sectionId);
        } catch (Exception e) {
            log.warn("Remote releaseSeat failed for section {}: {}. Executing fallback release.", sectionId, e.getMessage());
            fallbackClient.releaseSeat(sectionId);
        }
    }

    private CourseSectionSnapshot mapToSnapshot(RemoteCourseSectionDto dto) {
        CourseSectionSnapshot snapshot = new CourseSectionSnapshot();
        snapshot.setSectionId(dto.getId() != null ? dto.getId() : 0L);
        snapshot.setCourseId(dto.getCourseId() != null ? dto.getCourseId() : 0L);
        snapshot.setCourseCode(dto.getCourseCode() != null ? dto.getCourseCode() : "CS101");
        snapshot.setTitle(dto.getCourseTitle() != null ? dto.getCourseTitle() : "Course");
        snapshot.setSemester(dto.getSemester() != null ? dto.getSemester() : "FALL");
        snapshot.setYear(dto.getYear() != null ? dto.getYear() : 2026);
        snapshot.setScheduleDays(dto.getScheduleDays() != null ? dto.getScheduleDays() : "MWF");
        snapshot.setStartTime(parseTime(dto.getStartTime()));
        snapshot.setEndTime(parseTime(dto.getEndTime()));
        snapshot.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 30);
        snapshot.setEnrolledCount(dto.getEnrolledCount() != null ? dto.getEnrolledCount() : 0);
        snapshot.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");
        if (dto.getPrerequisiteCourseCodes() != null && !dto.getPrerequisiteCourseCodes().isEmpty()) {
            snapshot.setPrerequisites(String.join(",", dto.getPrerequisiteCourseCodes()));
        }
        return snapshot;
    }

    private Time parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return Time.valueOf("09:00:00");
        }
        try {
            if (timeStr.length() == 5) {
                return Time.valueOf(timeStr + ":00");
            }
            return Time.valueOf(timeStr);
        } catch (Exception e) {
            return Time.valueOf("09:00:00");
        }
    }
}
