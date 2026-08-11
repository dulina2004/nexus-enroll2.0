package com.nexusenroll.student.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO mirroring the academic record payload returned by the Academic Record Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteAcademicRecordDto {
    private Long studentId;
    private Double cumulativeGpa;
    private Integer totalCreditsAttempted;
    private Integer totalCreditsEarned;
    private Integer totalCreditsPassed;
    private Integer totalCreditsFailed;
    private String graduationStatus;
}
