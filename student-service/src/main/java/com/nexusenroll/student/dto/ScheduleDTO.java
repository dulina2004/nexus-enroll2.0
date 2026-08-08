package com.nexusenroll.student.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
