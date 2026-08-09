package com.nexusenroll.enrollment.client;

import com.nexusenroll.enrollment.client.dto.RemoteStudentDto;

import java.util.Optional;

public interface StudentClient {
    Optional<RemoteStudentDto> getStudentById(long studentId);
}
