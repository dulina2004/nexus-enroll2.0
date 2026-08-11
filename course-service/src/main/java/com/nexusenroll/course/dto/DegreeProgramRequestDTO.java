package com.nexusenroll.course.dto;

import lombok.*;

/**
 * Payload for creating or updating a degree program, consumed by the degree program
 * create/update operations; some fields carry client-facing aliases (e.g. {@code
 * programCode} for {@code code}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeProgramRequestDTO {
    private String code;
    private String programCode; // alias
    private String name;
    private String degreeType;
    private Integer totalCreditsRequired;
    private Integer requiredCredits; // alias
    private Long departmentId;
    private String status;
}
