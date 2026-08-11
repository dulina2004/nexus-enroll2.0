package com.nexusenroll.academicrecord.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** DTO for a single grade record, used both as request payload and API response. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRecordDto {

    private Long id;

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    private Long studentId;
    private String courseCode;
    private String courseTitle;
    private String assignmentTitle;
    private Double maxPoints;
    private Double pointsEarned;
    private String letterGrade;
    private LocalDate gradedDate;
    private String gradedBy;
    private String comments;
}
