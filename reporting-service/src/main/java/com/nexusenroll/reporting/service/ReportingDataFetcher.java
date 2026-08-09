package com.nexusenroll.reporting.service;

import com.nexusenroll.reporting.client.CourseClient;
import com.nexusenroll.reporting.client.EnrollmentClient;
import com.nexusenroll.reporting.client.FacultyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Helper component delegating to microservice HTTP clients.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportingDataFetcher {

    private final EnrollmentClient enrollmentClient;
    private final CourseClient courseClient;
    private final FacultyClient facultyClient;

    public Map<String, Object> fetchEnrollmentSummary(String semester, int year) {
        return enrollmentClient.getEnrollmentSummary(semester, year);
    }

    public List<Map<String, Object>> fetchEnrollmentsByCourse() {
        return courseClient.getCourses();
    }

    public List<Map<String, Object>> fetchFacultyWorkloadData(String semester, int year) {
        return facultyClient.getFacultyWorkload(semester, year);
    }
}
