package com.nexusenroll.academicrecord.client;

import com.nexusenroll.academicrecord.client.dto.RemoteStudentDto;

import java.util.Optional;

public interface StudentClient {
    Optional<RemoteStudentDto> getStudentById(long studentId);
}
