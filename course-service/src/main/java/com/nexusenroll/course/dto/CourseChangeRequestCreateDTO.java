package com.nexusenroll.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload for submitting a new course change request, consumed by the create-request
 * operation; several fields carry client-facing aliases (e.g. {@code facultyId} for
 * {@code requestedBy}, {@code reason} for {@code justification}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseChangeRequestCreateDTO {
    @NotNull(message = "Course ID is required")
    private Long courseId;
    private Long sectionId;
    private Long requestedBy;
    private Long facultyId; // alias
    private String requestType; // CAPACITY | DESCRIPTION | PREREQUISITE
    private String currentValue;
    private String proposedValue;
    private String proposedChanges; // alias
    private String justification;
    private String reason; // alias
}
