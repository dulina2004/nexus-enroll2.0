package com.nexusenroll.reporting.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.reporting.dto.AuditReportResponseDto;
import com.nexusenroll.reporting.dto.ReportResponseDto;
import com.nexusenroll.reporting.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportingService reportingService;

    @InjectMocks
    private ReportingController reportingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /reports/enrollment-stats returns 200 OK")
    void testGetEnrollmentStats() throws Exception {
        ReportResponseDto dto = ReportResponseDto.builder()
                .title("Enrollment Report")
                .reportType("ENROLLMENT_STATISTICS")
                .semester("SPRING")
                .year(2026)
                .generatedAt(LocalDateTime.now())
                .summaryMetrics(Map.of("totalStudents", 100))
                .detailsData(List.of())
                .build();

        when(reportingService.generateEnrollmentReport("SPRING", 2026)).thenReturn(dto);

        mockMvc.perform(get("/api/reports/enrollment-stats?semester=SPRING&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.reportType").value("ENROLLMENT_STATISTICS"));
    }

    @Test
    @DisplayName("GET /api/reports/course-popularity returns 200 OK")
    void testGetCoursePopularity() throws Exception {
        ReportResponseDto dto = ReportResponseDto.builder()
                .title("Course Popularity")
                .reportType("COURSE_POPULARITY")
                .semester("SPRING")
                .year(2026)
                .generatedAt(LocalDateTime.now())
                .summaryMetrics(Map.of("totalCourses", 10))
                .detailsData(List.of())
                .build();

        when(reportingService.generateCoursePopularityReport("SPRING", 2026)).thenReturn(dto);

        mockMvc.perform(get("/api/reports/course-popularity?semester=SPRING&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.reportType").value("COURSE_POPULARITY"));
    }

    @Test
    @DisplayName("GET /api/reports/faculty-workload returns 200 OK")
    void testGetFacultyWorkload() throws Exception {
        ReportResponseDto dto = ReportResponseDto.builder()
                .title("Faculty Workload")
                .reportType("FACULTY_WORKLOAD")
                .semester("SPRING")
                .year(2026)
                .generatedAt(LocalDateTime.now())
                .summaryMetrics(Map.of("facultyCount", 5))
                .detailsData(List.of())
                .build();

        when(reportingService.generateFacultyWorkloadReport("SPRING", 2026)).thenReturn(dto);

        mockMvc.perform(get("/api/reports/faculty-workload?semester=SPRING&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.reportType").value("FACULTY_WORKLOAD"));
    }

    @Test
    @DisplayName("GET /api/reports/audit-history returns 200 OK")
    void testGetAllAuditReports() throws Exception {
        AuditReportResponseDto audit = AuditReportResponseDto.builder()
                .id(1L)
                .reportType("ENROLLMENT_STATISTICS")
                .title("Audit Title")
                .semester("SPRING")
                .year(2026)
                .generatedBy("SYSTEM")
                .generatedAt(LocalDateTime.now())
                .build();

        when(reportingService.getAllSavedReports()).thenReturn(List.of(audit));

        mockMvc.perform(get("/api/reports/audit-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }
}
