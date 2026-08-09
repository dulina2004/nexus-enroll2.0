package com.nexusenroll.reporting.integration;

import com.nexusenroll.reporting.client.CourseClient;
import com.nexusenroll.reporting.client.EnrollmentClient;
import com.nexusenroll.reporting.client.FacultyClient;
import com.nexusenroll.reporting.repository.AuditReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReportingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditReportRepository auditReportRepository;

    @MockBean
    private CourseClient courseClient;

    @MockBean
    private EnrollmentClient enrollmentClient;

    @MockBean
    private FacultyClient facultyClient;

    @BeforeEach
    void setUp() {
        auditReportRepository.deleteAll();
    }

    @Test
    void shouldGenerateEnrollmentReportAndSaveAudit() throws Exception {
        when(courseClient.getCourses()).thenReturn(List.of());
        when(enrollmentClient.getEnrollmentSummary(anyString(), anyInt())).thenReturn(Map.of("totalEnrollments", 0));

        mockMvc.perform(get("/api/reports/enrollment-stats")
                .param("semester", "FALL")
                .param("year", "2026")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reportType").value("ENROLLMENT_STATISTICS"))
                .andExpect(jsonPath("$.data.title").exists());

        // Verify that it saved to the AuditReportRepository
        assertEquals(1, auditReportRepository.count(), "Audit report should be saved to database");
    }
}
