package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;

/**
 * Representation of a single degree program requirement returned to clients,
 * including the linked course's code and title.
 */
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
