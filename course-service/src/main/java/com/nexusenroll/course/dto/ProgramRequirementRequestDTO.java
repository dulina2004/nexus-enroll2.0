package com.nexusenroll.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload for adding a course requirement to a degree program, consumed by the
 * program-requirement create operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequirementRequestDTO {
    @NotNull(message = "Course ID is required")
    private Long courseId;

    @Builder.Default
    private String requirementType = "CORE";

    @Builder.Default
    private String minimumGrade = "C";
}
