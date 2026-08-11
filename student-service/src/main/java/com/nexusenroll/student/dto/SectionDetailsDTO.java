package com.nexusenroll.student.dto;

import lombok.*;

/**
 * DTO carrying course section details fetched from course-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDetailsDTO {
    private Long sectionId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String scheduleDays;
    private String startTime;
    private String endTime;
    private String location;
    private String semester;
    private Integer year;
}
