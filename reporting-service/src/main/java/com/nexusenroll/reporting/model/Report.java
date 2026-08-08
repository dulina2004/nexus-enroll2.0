package com.nexusenroll.reporting.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product class representing a generated report assembled step-by-step by ReportBuilder implementations.
 */
@Getter
@Setter
public class Report {

    private String title;
    private String reportType;
    private String semester;
    private int year;
    private LocalDateTime generatedAt;
    private Map<String, Object> summaryMetrics;
    private List<Map<String, Object>> detailsData;

    public Report() {
        this.generatedAt = LocalDateTime.now();
        this.summaryMetrics = new HashMap<>();
        this.detailsData = new ArrayList<>();
    }

    public Report(String title, String reportType, String semester, int year) {
        this.title = title;
        this.reportType = reportType;
        this.semester = semester;
        this.year = year;
        this.generatedAt = LocalDateTime.now();
        this.summaryMetrics = new HashMap<>();
        this.detailsData = new ArrayList<>();
    }

    public void addSummaryMetric(String key, Object value) {
        this.summaryMetrics.put(key, value);
    }

    public void addDataRow(Map<String, Object> row) {
        if (row != null) {
            this.detailsData.add(row);
        }
    }
}
