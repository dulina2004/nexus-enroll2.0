package com.nexusenroll.academicrecord.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** DTO for a student's degree-progress figures, used both as request payload and API response. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeProgressDto {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long programId;
    private Integer totalCreditsRequired;
    private Integer totalCreditsCompleted;
    private Integer generalEdCompleted;
    private Integer majorCreditsCompleted;
    private Integer electiveCreditsCompleted;
    private Double progressPercentage;
    private LocalDate expectedGraduationDate;
}
