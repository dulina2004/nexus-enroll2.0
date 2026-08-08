package com.nexusenroll.course.dto;

import lombok.*;

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
