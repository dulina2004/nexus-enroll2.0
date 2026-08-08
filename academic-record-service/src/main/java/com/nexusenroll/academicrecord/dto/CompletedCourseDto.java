package com.nexusenroll.academicrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedCourseDto {

    private Long id;
    private Long studentId;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    private String courseTitle;
    private Integer credits;
    private String grade;
    private String semester;
    private Integer year;
    private String instructorName;
}
