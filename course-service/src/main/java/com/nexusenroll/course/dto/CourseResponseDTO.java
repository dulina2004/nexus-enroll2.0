package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Full representation of a course returned to clients, including denormalized
 * department info and resolved prerequisite/co-requisite details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private Long id;
    private String courseCode;
    private Integer courseNumber;
    private String title;
    private String description;
    private Integer credits;
    private Integer capacity;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String level;
    private String status;
    private String prerequisites;
    private String coRequisites;
    private List<PrerequisiteDTO> prerequisiteDetails;
    private List<PrerequisiteDTO> coRequisiteDetails;
    private Instant createdAt;
    private Instant updatedAt;
}
