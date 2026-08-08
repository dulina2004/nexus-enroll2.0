package com.nexusenroll.faculty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchGradeResultDto {
    private int totalSubmitted;
    private int successCount;
    private int failureCount;

    @Builder.Default
    private List<Long> submittedGradeIds = new ArrayList<>();

    @Builder.Default
    private List<FailedGradeDto> failures = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedGradeDto {
        private long enrollmentId;
        private String submittedValue;
        private String reason;
    }
}
