package com.nexusenroll.faculty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDto {
    private Long id;
    private Long userId;
    private String facultyId;
    private String title;
    private Long departmentId;
    private String officeLocation;
    private String officePhone;
    private String researchInterests;
    private String bio;
    private String degreeLevel;
    private Integer yearsExperience;
    private String employmentType;
    private String status;
    private LocalDate hireDate;
}
