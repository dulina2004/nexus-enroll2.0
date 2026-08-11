package com.nexusenroll.course.dto;

import lombok.*;

/**
 * Carries a single prerequisite (or co-requisite, when {@code corequisite} is set)
 * relationship between a course and a required course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisiteDTO {
    private String courseCode;
    private String prerequisiteCode;
    private boolean corequisite;
}
