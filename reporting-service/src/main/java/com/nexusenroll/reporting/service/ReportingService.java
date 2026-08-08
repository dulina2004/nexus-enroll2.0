package com.nexusenroll.reporting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.reporting.builder.CoursePopularityReportBuilder;
import com.nexusenroll.reporting.builder.EnrollmentReportBuilder;
import com.nexusenroll.reporting.builder.FacultyWorkloadReportBuilder;
import com.nexusenroll.reporting.builder.ReportBuilder;
import com.nexusenroll.reporting.dto.AuditReportResponseDto;
import com.nexusenroll.reporting.dto.ReportResponseDto;
import com.nexusenroll.reporting.model.AuditReport;
import com.nexusenroll.reporting.model.Report;
import com.nexusenroll.reporting.repository.AuditReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final ReportingDataFetcher dataFetcher;
    private final AuditReportRepository auditReportRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Transactional
    public ReportResponseDto generateEnrollmentReport(String semester, int year) {
        String sem = (semester == null || semester.isBlank()) ? "SPRING" : semester.toUpperCase();
        int yr = year <= 0 ? 2026 : year;

        Map<String, Object> summary = dataFetcher.fetchEnrollmentSummary(sem, yr);
        List<Map<String, Object>> courseRows = dataFetcher.fetchEnrollmentsByCourse();

        // Step-by-step assembly using Builder Design Pattern
        ReportBuilder builder = new EnrollmentReportBuilder()
                .setTitle("Enrollment Statistics Report - " + sem + " " + yr)
                .setSemesterAndYear(sem, yr);

        summary.forEach(builder::addSummaryMetric);
        courseRows.forEach(builder::addDataRow);

        Report report = builder.build();
        saveAuditReport(report);
        return mapToReportResponseDto(report);
    }

    @Transactional
    public ReportResponseDto generateCoursePopularityReport(String semester, int year) {
        String sem = (semester == null || semester.isBlank()) ? "SPRING" : semester.toUpperCase();
        int yr = year <= 0 ? 2026 : year;

        List<Map<String, Object>> courseRows = dataFetcher.fetchEnrollmentsByCourse();

        // Step-by-step assembly using Builder Design Pattern
        ReportBuilder builder = new CoursePopularityReportBuilder()
                .setTitle("Course Popularity & Demand Report - " + sem + " " + yr)
                .setSemesterAndYear(sem, yr)
                .addSummaryMetric("totalCoursesAnalyzed", courseRows.size());

        courseRows.forEach(builder::addDataRow);

        Report report = builder.build();
        saveAuditReport(report);
        return mapToReportResponseDto(report);
    }

    @Transactional
    public ReportResponseDto generateFacultyWorkloadReport(String semester, int year) {
        String sem = (semester == null || semester.isBlank()) ? "SPRING" : semester.toUpperCase();
        int yr = year <= 0 ? 2026 : year;

        List<Map<String, Object>> workloadRows = dataFetcher.fetchFacultyWorkloadData(sem, yr);

        int totalCoursesTaught = workloadRows.stream()
                .mapToInt(r -> {
                    Object val = r.getOrDefault("coursesTaught", 0);
                    return val instanceof Number ? ((Number) val).intValue() : 0;
                })
                .sum();

        // Step-by-step assembly using Builder Design Pattern
        ReportBuilder builder = new FacultyWorkloadReportBuilder()
                .setTitle("Faculty Workload Report - " + sem + " " + yr)
                .setSemesterAndYear(sem, yr)
                .addSummaryMetric("facultyCount", workloadRows.size())
                .addSummaryMetric("totalCoursesAssigned", totalCoursesTaught);

        workloadRows.forEach(builder::addDataRow);

        Report report = builder.build();
        saveAuditReport(report);
        return mapToReportResponseDto(report);
    }

    @Transactional(readOnly = true)
    public List<AuditReportResponseDto> getAllSavedReports() {
        return auditReportRepository.findAll().stream()
                .map(this::mapToAuditResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuditReportResponseDto getSavedReportById(Long id) {
        AuditReport audit = auditReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit report not found: " + id));
        return mapToAuditResponseDto(audit);
    }

    private void saveAuditReport(Report report) {
        try {
            String reportJson = objectMapper.writeValueAsString(report);
            AuditReport audit = AuditReport.builder()
                    .reportType(report.getReportType())
                    .title(report.getTitle())
                    .semester(report.getSemester())
                    .year(report.getYear())
                    .reportData(reportJson)
                    .generatedBy("SYSTEM")
                    .build();
            auditReportRepository.save(audit);
        } catch (Exception e) {
            log.error("Could not persist audit report: {}", e.getMessage(), e);
        }
    }

    private ReportResponseDto mapToReportResponseDto(Report report) {
        return ReportResponseDto.builder()
                .title(report.getTitle())
                .reportType(report.getReportType())
                .semester(report.getSemester())
                .year(report.getYear())
                .generatedAt(report.getGeneratedAt())
                .summaryMetrics(report.getSummaryMetrics())
                .detailsData(report.getDetailsData())
                .build();
    }

    private AuditReportResponseDto mapToAuditResponseDto(AuditReport audit) {
        return AuditReportResponseDto.builder()
                .id(audit.getId())
                .reportType(audit.getReportType())
                .title(audit.getTitle())
                .semester(audit.getSemester())
                .year(audit.getYear())
                .reportData(audit.getReportData())
                .generatedBy(audit.getGeneratedBy())
                .generatedAt(audit.getGeneratedAt())
                .build();
    }
}
