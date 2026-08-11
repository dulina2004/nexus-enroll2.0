package com.nexusenroll.enrollment.client;

import com.nexusenroll.enrollment.client.dto.RemoteStudentDto;

import java.util.Optional;

/**
 * Abstraction over the remote Student Service, used to look up a student's profile
 * during enrollment processing.
 */
public interface StudentClient {
    Optional<RemoteStudentDto> getStudentById(long studentId);
}
