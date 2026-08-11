package com.nexusenroll.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO representing a generated {@link com.nexusenroll.reporting.model.Report}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {

    private String title;
    private String reportType;
    private String semester;
    private int year;
    private LocalDateTime generatedAt;
    private Map<String, Object> summaryMetrics;
    private List<Map<String, Object>> detailsData;
}
