package com.nexusenroll.reporting.builder;

import com.nexusenroll.reporting.model.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportBuilderTest {

    @Test
    @DisplayName("EnrollmentReportBuilder constructs ENROLLMENT_STATISTICS report step-by-step")
    void testEnrollmentReportBuilder() {
        Report report = new EnrollmentReportBuilder()
                .setTitle("Spring 2026 Enrollment Report")
                .setSemesterAndYear("SPRING", 2026)
                .addSummaryMetric("totalStudents", 500)
                .addDataRow(Map.of("course", "CS101", "enrolled", 120))
                .build();

        assertNotNull(report);
        assertEquals("ENROLLMENT_STATISTICS", report.getReportType());
        assertEquals("Spring 2026 Enrollment Report", report.getTitle());
        assertEquals("SPRING", report.getSemester());
        assertEquals(2026, report.getYear());
        assertEquals(500, report.getSummaryMetrics().get("totalStudents"));
        assertEquals(1, report.getDetailsData().size());
    }

    @Test
    @DisplayName("FacultyWorkloadReportBuilder constructs FACULTY_WORKLOAD report step-by-step")
    void testFacultyWorkloadReportBuilder() {
        Report report = new FacultyWorkloadReportBuilder()
                .setTitle("Faculty Teaching Workload")
                .setSemesterAndYear("FALL", 2026)
                .addSummaryMetric("facultyCount", 20)
                .addDataRow(Map.of("facultyName", "Dr. Alan Turing", "sections", 3))
                .build();

        assertNotNull(report);
        assertEquals("FACULTY_WORKLOAD", report.getReportType());
        assertEquals("Faculty Teaching Workload", report.getTitle());
        assertEquals(20, report.getSummaryMetrics().get("facultyCount"));
        assertEquals(1, report.getDetailsData().size());
    }

    @Test
    @DisplayName("CoursePopularityReportBuilder constructs COURSE_POPULARITY report step-by-step")
    void testCoursePopularityReportBuilder() {
        Report report = new CoursePopularityReportBuilder()
                .setTitle("Course Demand Report")
                .setSemesterAndYear("SPRING", 2026)
                .addSummaryMetric("analyzedCourses", 15)
                .addDataRow(Map.of("courseCode", "CS101", "popularityIndex", 98))
                .build();

        assertNotNull(report);
        assertEquals("COURSE_POPULARITY", report.getReportType());
        assertEquals("Course Demand Report", report.getTitle());
        assertEquals(15, report.getSummaryMetrics().get("analyzedCourses"));
        assertEquals(1, report.getDetailsData().size());
    }
}
