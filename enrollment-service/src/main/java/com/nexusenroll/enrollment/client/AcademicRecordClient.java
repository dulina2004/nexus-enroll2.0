package com.nexusenroll.enrollment.client;

import com.nexusenroll.enrollment.client.dto.RemoteCompletedCourseDto;

import java.util.List;

public interface AcademicRecordClient {
    List<RemoteCompletedCourseDto> getCompletedCourses(long studentId);
}
