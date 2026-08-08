package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseChangeRequestResponseDTO {
    private Long id;
    private Long courseId;
    private Long sectionId;
    private Long requestedBy;
    private Long facultyId; // alias
    private String requestType;
    private String currentValue;
    private String proposedValue;
    private String proposedChanges; // alias
    private String justification;
    private String reason; // alias
    private String status;
    private Long reviewedBy;
    private Instant reviewedAt;
    private String reviewComment;
    private Instant createdAt;
}
