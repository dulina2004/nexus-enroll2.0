package com.nexusenroll.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO representing a persisted {@link com.nexusenroll.reporting.model.AuditReport} audit record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditReportResponseDto {

    private Long id;
    private String reportType;
    private String title;
    private String semester;
    private Integer year;
    private String reportData;
    private String generatedBy;
    private LocalDateTime generatedAt;
}
