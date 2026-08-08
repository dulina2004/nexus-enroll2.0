package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSectionResponseDTO {
    private Long id;
    private Long courseId;
    private String sectionNumber;
    private Long instructorId;
    private String semester;
    private Integer year;
    private String scheduleDays;
    private String startTime;
    private String endTime;
    private String location;
    private Integer capacity;
    private Integer enrolledCount;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
