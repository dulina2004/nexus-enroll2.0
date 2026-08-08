package com.nexusenroll.course.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseChangeRequestReviewDTO {
    private Long adminUserId;
    private String comment;
}
