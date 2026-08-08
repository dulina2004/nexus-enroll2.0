package com.nexusenroll.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateCapacityDTO {
    @NotNull(message = "Capacity is required")
    @Min(value = 0, message = "Capacity must be zero or greater")
    private Integer capacity;
}
