package com.nexusenroll.faculty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload identifying the grade to submit, approve, or reject.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeOperationDto {
    private Long gradeId;
}
