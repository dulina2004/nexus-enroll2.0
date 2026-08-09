package com.nexusenroll.enrollment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
