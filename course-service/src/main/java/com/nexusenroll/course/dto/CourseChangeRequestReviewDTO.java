package com.nexusenroll.course.dto;

import lombok.*;

/**
 * Payload for reviewing (approving or rejecting) a pending course change request,
 * carrying the reviewing admin's ID and an optional comment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseChangeRequestReviewDTO {
    private Long adminUserId;
    private String comment;
}
