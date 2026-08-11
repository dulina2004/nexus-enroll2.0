package com.nexusenroll.enrollment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Completed course record as returned by the Academic Record Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteCompletedCourseDto {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Double credits;
    private String grade;
    private String semester;
    private Integer year;
}
