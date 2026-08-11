package com.nexusenroll.enrollment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Student profile data as returned by the Student Service.
 */
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
}
