package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequirementResponseDTO {
    private Long id;
    private Long programId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String requirementType;
    private String minimumGrade;
    private Instant createdAt;
}
