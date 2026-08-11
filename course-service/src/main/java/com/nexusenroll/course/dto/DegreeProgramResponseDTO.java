package com.nexusenroll.course.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Full representation of a degree program returned to clients, including its list of
 * program requirements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeProgramResponseDTO {
    private Long id;
    private String code;
    private String programCode; // alias
    private String name;
    private String degreeType;
    private Integer totalCreditsRequired;
    private Integer requiredCredits; // alias
    private Long departmentId;
    private String status;
    private Instant createdAt;
    private List<ProgramRequirementResponseDTO> requirements;
}
