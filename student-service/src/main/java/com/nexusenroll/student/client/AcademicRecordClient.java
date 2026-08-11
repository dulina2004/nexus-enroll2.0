package com.nexusenroll.student.client;

import com.nexusenroll.student.client.dto.RemoteAcademicRecordDto;

import java.util.Optional;

/**
 * Abstraction over the remote Academic Record Service, used to look up a student's
 * cumulative academic record.
 */
public interface AcademicRecordClient {
    Optional<RemoteAcademicRecordDto> getAcademicRecord(long studentId);
}
