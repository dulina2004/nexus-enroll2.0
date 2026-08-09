package com.nexusenroll.faculty.service;

import com.nexusenroll.faculty.client.AcademicRecordClient;
import com.nexusenroll.faculty.state.GradeContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service publisher responsible for notifying the Academic Record Service
 * when a grade enters the APPROVED state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AcademicRecordPublisher {

    private final AcademicRecordClient academicRecordClient;

    /**
     * Publishes/Records an approved grade to the Academic Record Service.
     */
    public void notifyGradeApproved(GradeContext grade) {
        log.info("Academic Record Service notified: Grade APPROVED for enrollment ID {}, assignment '{}', letter grade '{}'",
                grade.getEnrollmentId(), grade.getAssignmentTitle(), grade.getLetterGrade());
        academicRecordClient.recordGrade(grade);
    }
}
