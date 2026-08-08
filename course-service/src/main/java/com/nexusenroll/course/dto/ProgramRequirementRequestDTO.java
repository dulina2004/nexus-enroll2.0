package com.nexusenroll.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
