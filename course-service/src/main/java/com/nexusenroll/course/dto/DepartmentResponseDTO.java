package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;

/**
 * Representation of a department returned to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long headFacultyId;
    private Instant createdAt;
    private Instant updatedAt;
}
