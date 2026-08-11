package com.nexusenroll.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload for creating or updating a course, consumed by the course create/update
 * operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotNull(message = "Course number is required")
    @Min(value = 1, message = "Course number must be greater than zero")
    private Integer courseNumber;

    @NotBlank(message = "Course title is required")
    private String title;

    private String description;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be greater than zero")
    private Integer credits;

    @Min(value = 0, message = "Capacity must be zero or greater")
    private Integer capacity;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String level;
    private Object prerequisites; // Accepts String "CS101, CS102" or List<String>
    private Object coRequisites;   // Accepts String or List<String>
    private String status;
}
