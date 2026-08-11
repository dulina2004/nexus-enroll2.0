package com.nexusenroll.academicrecord.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO mirroring the student payload returned by the Student Service's API. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteStudentDto {
    private Long id;
    private Long userId;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String degreeProgram;
}
