package com.nexusenroll.student.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing a student's course schedule for a given semester and year.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long studentId;
    private String semester;
    private Integer year;

    @Builder.Default
    private List<ScheduleItemDTO> items = new ArrayList<>();
}
