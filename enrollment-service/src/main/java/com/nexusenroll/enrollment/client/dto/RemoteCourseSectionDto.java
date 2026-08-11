package com.nexusenroll.enrollment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Course section data as returned by the Course Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteCourseSectionDto {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String sectionNumber;
    private String instructorName;
    private String semester;
    private Integer year;
    private String scheduleDays;
    private String startTime;
    private String endTime;
    private String location;
    private Integer capacity;
    private Integer enrolledCount;
    private String status;
    private List<String> prerequisiteCourseCodes;
}
