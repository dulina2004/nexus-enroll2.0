package com.nexusenroll.reporting.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.reporting.dto.AuditReportResponseDto;
import com.nexusenroll.reporting.dto.ReportResponseDto;
import com.nexusenroll.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/enrollment-stats")
    public ResponseEntity<ApiResponse<ReportResponseDto>> getEnrollmentStats(
            @RequestParam(name = "semester", required = false) String semester,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {

        ReportResponseDto report = reportingService.generateEnrollmentReport(semester, year);
        return ResponseEntity.ok(ApiResponse.success("Enrollment statistics report generated successfully", report));
    }

    @GetMapping("/course-popularity")
    public ResponseEntity<ApiResponse<ReportResponseDto>> getCoursePopularity(
            @RequestParam(name = "semester", required = false) String semester,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {

        ReportResponseDto report = reportingService.generateCoursePopularityReport(semester, year);
        return ResponseEntity.ok(ApiResponse.success("Course popularity report generated successfully", report));
    }

    @GetMapping("/faculty-workload")
    public ResponseEntity<ApiResponse<ReportResponseDto>> getFacultyWorkload(
            @RequestParam(name = "semester", required = false) String semester,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {

        ReportResponseDto report = reportingService.generateFacultyWorkloadReport(semester, year);
        return ResponseEntity.ok(ApiResponse.success("Faculty workload report generated successfully", report));
    }

    @GetMapping("/audit-history")
    public ResponseEntity<ApiResponse<List<AuditReportResponseDto>>> getAllAuditReports() {
        List<AuditReportResponseDto> list = reportingService.getAllSavedReports();
        return ResponseEntity.ok(ApiResponse.success("Audit reports retrieved successfully", list));
    }

    @GetMapping("/audit-history/{id}")
    public ResponseEntity<ApiResponse<AuditReportResponseDto>> getAuditReportById(@PathVariable("id") Long id) {
        AuditReportResponseDto audit = reportingService.getSavedReportById(id);
        return ResponseEntity.ok(ApiResponse.success("Audit report retrieved successfully", audit));
    }
}
