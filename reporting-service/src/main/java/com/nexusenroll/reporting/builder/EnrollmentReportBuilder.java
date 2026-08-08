package com.nexusenroll.reporting.builder;

import com.nexusenroll.reporting.model.Report;

import java.util.Map;

/**
 * Concrete Builder for constructing Enrollment Statistics Reports.
 */
public class EnrollmentReportBuilder implements ReportBuilder {

    private final Report report;

    public EnrollmentReportBuilder() {
        this.report = new Report();
        this.report.setReportType("ENROLLMENT_STATISTICS");
        this.report.setTitle("Semester Enrollment Statistics Report");
    }

    @Override
    public ReportBuilder setTitle(String title) {
        this.report.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder setSemesterAndYear(String semester, int year) {
        this.report.setSemester(semester);
        this.report.setYear(year);
        return this;
    }

    @Override
    public ReportBuilder addSummaryMetric(String key, Object value) {
        this.report.addSummaryMetric(key, value);
        return this;
    }

    @Override
    public ReportBuilder addDataRow(Map<String, Object> row) {
        this.report.addDataRow(row);
        return this;
    }

    @Override
    public Report build() {
        return this.report;
    }
}
