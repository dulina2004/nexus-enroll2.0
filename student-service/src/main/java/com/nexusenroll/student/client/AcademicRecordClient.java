package com.nexusenroll.student.client;

import com.nexusenroll.student.client.dto.RemoteAcademicRecordDto;

import java.util.Optional;

public interface AcademicRecordClient {
    Optional<RemoteAcademicRecordDto> getAcademicRecord(long studentId);
}
