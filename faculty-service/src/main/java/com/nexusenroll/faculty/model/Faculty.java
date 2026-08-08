package com.nexusenroll.faculty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "faculty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "faculty_id", unique = true, length = 50)
    private String facultyId;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    @Column(name = "office_phone", length = 20)
    private String officePhone;

    @Column(name = "research_interests", columnDefinition = "TEXT")
    private String researchInterests;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "degree_level", length = 50)
    private String degreeLevel;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "employment_type", length = 50)
    private String employmentType;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "hire_date")
    private LocalDate hireDate;
}
