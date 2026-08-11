package com.nexusenroll.student.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO summarizing a student's progress toward degree completion, including credits and GPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeProgressDTO {
    private Long studentId;

    @Builder.Default
    private Integer completedCredits = 0;

    @Builder.Default
    private Integer requiredCredits = 120;

    @Builder.Default
    private Double gpa = 0.0;

    @Builder.Default
    private List<Object> completedCourses = new ArrayList<>();

    @Builder.Default
    private List<Object> remainingRequirements = new ArrayList<>();
}
