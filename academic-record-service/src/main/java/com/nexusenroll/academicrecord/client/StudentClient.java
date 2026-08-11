package com.nexusenroll.academicrecord.client;

import com.nexusenroll.academicrecord.client.dto.RemoteStudentDto;

import java.util.Optional;

/** Client abstraction for fetching student profile data from the Student Service. */
public interface StudentClient {
    Optional<RemoteStudentDto> getStudentById(long studentId);
}
