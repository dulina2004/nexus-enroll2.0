package com.nexusenroll.student.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating or updating a student's profile fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDate graduationDate;
    private String status;
}
