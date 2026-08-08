package com.nexusenroll.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
