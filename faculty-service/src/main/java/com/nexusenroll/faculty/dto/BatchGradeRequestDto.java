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
public class BatchGradeRequestDto {

    @Builder.Default
    private List<GradeItemDto> grades = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeItemDto {
        private Long enrollmentId;
        private Long studentId;
        private String grade;
        private String letterGrade;
        private String assignmentTitle;
        private Double pointsEarned;
        private Double maxPoints;
        private String gradedBy;

        public String getEffectiveGrade() {
            return grade != null && !grade.isBlank() ? grade : letterGrade;
        }
    }
}
