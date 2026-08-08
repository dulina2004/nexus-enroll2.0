package com.nexusenroll.reporting.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.reporting.dto.AuditReportResponseDto;
import com.nexusenroll.reporting.dto.ReportResponseDto;
import com.nexusenroll.reporting.model.AuditReport;
import com.nexusenroll.reporting.repository.AuditReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private ReportingDataFetcher dataFetcher;

    @Mock
    private AuditReportRepository auditReportRepository;

    @InjectMocks
    private ReportingService reportingService;

    @Test
    @DisplayName("generateEnrollmentReport uses Builder and saves audit entry")
    void testGenerateEnrollmentReport() {
        when(dataFetcher.fetchEnrollmentSummary("SPRING", 2026))
                .thenReturn(Map.of("totalEnrollments", 100, "activeStudents", 80));
        when(dataFetcher.fetchEnrollmentsByCourse())
                .thenReturn(List.of(Map.of("courseCode", "CS101", "studentCount", 50)));

        ReportResponseDto report = reportingService.generateEnrollmentReport("SPRING", 2026);

        assertNotNull(report);
        assertEquals("ENROLLMENT_STATISTICS", report.getReportType());
        assertEquals("SPRING", report.getSemester());
        assertEquals(2026, report.getYear());
        assertEquals(100, report.getSummaryMetrics().get("totalEnrollments"));

        verify(auditReportRepository).save(any(AuditReport.class));
    }

    @Test
    @DisplayName("generateFacultyWorkloadReport uses Builder and calculates summary metrics")
    void testGenerateFacultyWorkloadReport() {
        when(dataFetcher.fetchFacultyWorkloadData("FALL", 2026))
                .thenReturn(List.of(
                        Map.of("facultyId", "FAC-001", "coursesTaught", 3),
                        Map.of("facultyId", "FAC-002", "coursesTaught", 2)
                ));

        ReportResponseDto report = reportingService.generateFacultyWorkloadReport("FALL", 2026);

        assertNotNull(report);
        assertEquals("FACULTY_WORKLOAD", report.getReportType());
        assertEquals(2, report.getSummaryMetrics().get("facultyCount"));
        assertEquals(5, report.getSummaryMetrics().get("totalCoursesAssigned"));

        verify(auditReportRepository).save(any(AuditReport.class));
    }

    @Test
    @DisplayName("getSavedReportById returns AuditReportResponseDto")
    void testGetSavedReportById() {
        AuditReport audit = AuditReport.builder()
                .id(1L)
                .reportType("ENROLLMENT_STATISTICS")
                .title("Audit Title")
                .semester("SPRING")
                .year(2026)
                .build();

        when(auditReportRepository.findById(1L)).thenReturn(Optional.of(audit));

        AuditReportResponseDto result = reportingService.getSavedReportById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ENROLLMENT_STATISTICS", result.getReportType());
    }

    @Test
    @DisplayName("getSavedReportById throws ResourceNotFoundException for unknown ID")
    void testGetSavedReportByIdNotFound() {
        when(auditReportRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reportingService.getSavedReportById(999L));
    }
}
