package com.nexusenroll.course.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisiteDTO {
    private String courseCode;
    private String prerequisiteCode;
    private boolean corequisite;
}
