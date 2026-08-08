package com.nexusenroll.student.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItemDTO {
    private Long sectionId;
    private String courseCode;
    private String courseTitle;
    private String scheduleDays;
    private String startTime;
    private String endTime;
    private String location;
    private String status;
}
