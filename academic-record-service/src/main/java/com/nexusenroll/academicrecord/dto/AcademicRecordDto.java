package com.nexusenroll.academicrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Aggregated view of a student's academic record: GPA, credits, courses, grades and degree progress. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicRecordDto {

    private Long studentId;
    private Double cumulativeGpa;
    private Integer totalCreditsEarned;
    private Integer totalCreditsAttempted;
    private String graduationStatus;

    @Builder.Default
    private List<CompletedCourseDto> completedCourses = new ArrayList<>();

    @Builder.Default
    private List<GradeRecordDto> gradeHistory = new ArrayList<>();

    private DegreeProgressDto degreeProgress;
}
