package com.nexusenroll.academicrecord.service;

import com.nexusenroll.academicrecord.client.FacultyServiceClient;
import com.nexusenroll.academicrecord.dto.AcademicRecordDto;
import com.nexusenroll.academicrecord.dto.CompletedCourseDto;
import com.nexusenroll.academicrecord.dto.DegreeProgressDto;
import com.nexusenroll.academicrecord.dto.GradeRecordDto;
import com.nexusenroll.academicrecord.model.CompletedCourse;
import com.nexusenroll.academicrecord.model.CumulativeRecord;
import com.nexusenroll.academicrecord.model.DegreeProgress;
import com.nexusenroll.academicrecord.model.GradeRecord;
import com.nexusenroll.academicrecord.repository.CompletedCourseRepository;
import com.nexusenroll.academicrecord.repository.CumulativeRecordRepository;
import com.nexusenroll.academicrecord.repository.DegreeProgressRepository;
import com.nexusenroll.academicrecord.repository.GradeRecordRepository;
import com.nexusenroll.common.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicRecordServiceTest {

    @Mock
    private CompletedCourseRepository completedCourseRepository;

    @Mock
    private GradeRecordRepository gradeRecordRepository;

    @Mock
    private DegreeProgressRepository degreeProgressRepository;

    @Mock
    private CumulativeRecordRepository cumulativeRecordRepository;

    @Mock
    private FacultyServiceClient facultyServiceClient;

    @InjectMocks
    private AcademicRecordService academicRecordService;

    @Test
    @DisplayName("addCompletedCourse adds course, updates degree progress and cumulative record")
    void testAddCompletedCourse() {
        CompletedCourseDto dto = CompletedCourseDto.builder()
                .courseCode("CS101")
                .courseTitle("Intro to Computer Science")
                .credits(4)
                .grade("A")
                .semester("FALL")
                .year(2025)
                .build();

        CompletedCourse saved = CompletedCourse.builder()
                .id(1L)
                .studentId(101L)
                .courseCode("CS101")
                .courseTitle("Intro to Computer Science")
                .credits(4)
                .grade("A")
                .semester("FALL")
                .year(2025)
                .build();

        when(completedCourseRepository.save(any(CompletedCourse.class))).thenReturn(saved);
        when(degreeProgressRepository.findByStudentId(101L)).thenReturn(Optional.empty());
        when(completedCourseRepository.findByStudentIdOrderByYearDescSemesterDesc(101L)).thenReturn(List.of(saved));
        when(cumulativeRecordRepository.findByStudentId(101L)).thenReturn(Optional.empty());

        CompletedCourseDto result = academicRecordService.addCompletedCourse(101L, dto);

        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        assertEquals(4, result.getCredits());

        verify(degreeProgressRepository).save(any(DegreeProgress.class));
        verify(cumulativeRecordRepository).save(any(CumulativeRecord.class));
    }

    @Test
    @DisplayName("addCompletedCourse throws ValidationException when studentId is invalid")
    void testAddCompletedCourseInvalidStudent() {
        CompletedCourseDto dto = CompletedCourseDto.builder().courseCode("CS101").build();

        assertThrows(ValidationException.class, () -> academicRecordService.addCompletedCourse(0L, dto));
        assertThrows(ValidationException.class, () -> academicRecordService.addCompletedCourse(null, dto));
    }

    @Test
    @DisplayName("saveGrade saves grade record successfully")
    void testSaveGrade() {
        GradeRecordDto dto = GradeRecordDto.builder()
                .enrollmentId(50L)
                .studentId(101L)
                .assignmentTitle("Midterm Exam")
                .pointsEarned(88.0)
                .maxPoints(100.0)
                .letterGrade("B+")
                .build();

        GradeRecord saved = GradeRecord.builder()
                .id(10L)
                .enrollmentId(50L)
                .studentId(101L)
                .assignmentTitle("Midterm Exam")
                .pointsEarned(88.0)
                .maxPoints(100.0)
                .letterGrade("B+")
                .build();

        when(gradeRecordRepository.save(any(GradeRecord.class))).thenReturn(saved);

        GradeRecordDto result = academicRecordService.saveGrade(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Midterm Exam", result.getAssignmentTitle());
    }

    @Test
    @DisplayName("getAcademicRecord aggregates cumulative record, completed courses, grade history, and degree progress")
    void testGetAcademicRecord() {
        CumulativeRecord cumulative = CumulativeRecord.builder()
                .studentId(101L)
                .cumulativeGpa(3.8)
                .totalCreditsEarned(30)
                .totalCreditsAttempted(30)
                .graduationStatus("IN_PROGRESS")
                .build();

        CompletedCourse course = CompletedCourse.builder()
                .id(1L)
                .studentId(101L)
                .courseCode("CS101")
                .credits(3)
                .build();

        DegreeProgress progress = DegreeProgress.builder()
                .studentId(101L)
                .totalCreditsRequired(120)
                .totalCreditsCompleted(30)
                .progressPercentage(25.0)
                .build();

        when(cumulativeRecordRepository.findByStudentId(101L)).thenReturn(Optional.of(cumulative));
        when(completedCourseRepository.findByStudentIdOrderByYearDescSemesterDesc(101L)).thenReturn(List.of(course));
        when(gradeRecordRepository.findByStudentIdOrderByIdDesc(101L)).thenReturn(List.of());
        when(degreeProgressRepository.findByStudentId(101L)).thenReturn(Optional.of(progress));

        AcademicRecordDto record = academicRecordService.getAcademicRecord(101L);

        assertNotNull(record);
        assertEquals(101L, record.getStudentId());
        assertEquals(3.8, record.getCumulativeGpa());
        assertEquals(1, record.getCompletedCourses().size());
        assertNotNull(record.getDegreeProgress());
        assertEquals(25.0, record.getDegreeProgress().getProgressPercentage());
    }
}
