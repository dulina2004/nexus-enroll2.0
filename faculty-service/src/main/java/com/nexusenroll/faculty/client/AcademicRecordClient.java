package com.nexusenroll.faculty.client;

import com.nexusenroll.faculty.state.GradeContext;

/**
 * Client contract for forwarding an approved grade to the Academic Record Service.
 */
public interface AcademicRecordClient {
    void recordGrade(GradeContext grade);
}
