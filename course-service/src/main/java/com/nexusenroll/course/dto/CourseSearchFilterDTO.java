package com.nexusenroll.course.dto;

import lombok.*;

/**
 * Carries the search/filter criteria (keyword, department, course number, instructor,
 * semester, year) used for dynamic course search.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSearchFilterDTO {
    private String keyword;
    private String departmentCode;
    private Integer courseNumber;
    private Long instructorId;
    private String instructorName;
    private String semester;
    private Integer year;
}
