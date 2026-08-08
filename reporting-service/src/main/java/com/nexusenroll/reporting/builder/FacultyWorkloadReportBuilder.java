package com.nexusenroll.reporting.builder;

import com.nexusenroll.reporting.model.Report;

import java.util.Map;

/**
 * Concrete Builder for constructing Faculty Workload Reports.
 */
public class FacultyWorkloadReportBuilder implements ReportBuilder {

    private final Report report;

    public FacultyWorkloadReportBuilder() {
        this.report = new Report();
        this.report.setReportType("FACULTY_WORKLOAD");
        this.report.setTitle("Faculty Teaching Workload Report");
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
