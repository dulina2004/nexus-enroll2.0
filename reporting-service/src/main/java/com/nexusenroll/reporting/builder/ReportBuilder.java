package com.nexusenroll.reporting.builder;

import com.nexusenroll.reporting.model.Report;

import java.util.Map;

/**
 * Interface for the Builder Design Pattern in report generation.
 * Enables step-by-step assembly of complex Report instances.
 */
public interface ReportBuilder {

    ReportBuilder setTitle(String title);

    ReportBuilder setSemesterAndYear(String semester, int year);

    ReportBuilder addSummaryMetric(String key, Object value);

    ReportBuilder addDataRow(Map<String, Object> row);

    Report build();
}
