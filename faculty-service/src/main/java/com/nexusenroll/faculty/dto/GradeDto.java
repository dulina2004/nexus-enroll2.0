package com.nexusenroll.faculty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private Long id;
    private long enrollmentId;
    private long studentId;
    private long sectionId;
    private String assignmentTitle;
    private double pointsEarned;
    private double maxPoints;
    private String letterGrade;
    private String comments;
    private String gradedBy;
    private String status;
    private boolean canEdit;
}
