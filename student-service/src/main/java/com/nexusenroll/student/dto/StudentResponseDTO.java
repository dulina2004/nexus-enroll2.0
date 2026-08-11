package com.nexusenroll.student.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO returned to clients containing a student's full profile data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {
    private Long id;
    private Long userId;
    private String studentId;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDate enrollmentDate;
    private LocalDate graduationDate;
    private String status;
    private BigDecimal gpa;
    private Integer totalCreditsEarned;
    private Instant createdAt;
    private Instant updatedAt;
}
