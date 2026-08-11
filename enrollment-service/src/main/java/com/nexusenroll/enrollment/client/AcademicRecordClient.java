package com.nexusenroll.enrollment.client;

import com.nexusenroll.enrollment.client.dto.RemoteCompletedCourseDto;

import java.util.List;

/**
 * Abstraction over the remote Academic Record Service, used to look up a student's
 * completed courses when validating prerequisites during enrollment.
 */
public interface AcademicRecordClient {
    List<RemoteCompletedCourseDto> getCompletedCourses(long studentId);
}
