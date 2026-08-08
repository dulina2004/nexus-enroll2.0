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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicRecordService {

    private final CompletedCourseRepository completedCourseRepository;
    private final GradeRecordRepository gradeRecordRepository;
    private final DegreeProgressRepository degreeProgressRepository;
    private final CumulativeRecordRepository cumulativeRecordRepository;
    private final FacultyServiceClient facultyServiceClient;

    @Transactional
    public CompletedCourseDto addCompletedCourse(Long studentId, CompletedCourseDto dto) {
        if (studentId == null || studentId <= 0) {
            throw new ValidationException("Valid student ID is required");
        }
        if (dto == null || dto.getCourseCode() == null || dto.getCourseCode().isBlank()) {
            throw new ValidationException("Course code is required");
        }

        CompletedCourse course = CompletedCourse.builder()
                .studentId(studentId)
                .courseCode(dto.getCourseCode())
                .courseTitle(dto.getCourseTitle())
                .credits(dto.getCredits() != null ? dto.getCredits() : 3)
                .grade(dto.getGrade() != null ? dto.getGrade() : "A")
                .semester(dto.getSemester() != null ? dto.getSemester() : "SPRING")
                .year(dto.getYear() != null ? dto.getYear() : 2026)
                .instructorName(dto.getInstructorName() != null ? dto.getInstructorName() : "Instructor")
                .build();

        CompletedCourse savedCourse = completedCourseRepository.save(course);

        // Update total credits completed in degree progress
        DegreeProgress progress = degreeProgressRepository.findByStudentId(studentId)
                .orElse(DegreeProgress.builder()
                        .studentId(studentId)
                        .programId(1L)
                        .totalCreditsRequired(120)
                        .totalCreditsCompleted(0)
                        .generalEdCompleted(0)
                        .majorCreditsCompleted(0)
                        .electiveCreditsCompleted(0)
                        .progressPercentage(0.0)
                        .build());

        int newCredits = progress.getTotalCreditsCompleted() != null
                ? progress.getTotalCreditsCompleted() + savedCourse.getCredits()
                : savedCourse.getCredits();

        progress.setTotalCreditsCompleted(newCredits);
        progress.calculatePercentage();
        degreeProgressRepository.save(progress);

        // Update cumulative record credits
        List<CompletedCourse> allCompleted = completedCourseRepository.findByStudentIdOrderByYearDescSemesterDesc(studentId);
        int totalCredits = allCompleted.stream().mapToInt(c -> c.getCredits() != null ? c.getCredits() : 0).sum();

        CumulativeRecord cumulative = cumulativeRecordRepository.findByStudentId(studentId)
                .orElse(CumulativeRecord.builder()
                        .studentId(studentId)
                        .cumulativeGpa(3.5)
                        .totalCreditsEarned(0)
                        .totalCreditsAttempted(0)
                        .totalCreditsPassed(0)
                        .totalCreditsFailed(0)
                        .graduationStatus("IN_PROGRESS")
                        .build());

        cumulative.setTotalCreditsEarned(totalCredits);
        cumulative.setTotalCreditsAttempted(totalCredits);
        cumulative.setTotalCreditsPassed(totalCredits);
        cumulative.setGraduationStatus(totalCredits >= 120 ? "GRADUATED" : "IN_PROGRESS");
        cumulativeRecordRepository.save(cumulative);

        return mapToCompletedCourseDto(savedCourse);
    }

    @Transactional
    public GradeRecordDto saveGrade(GradeRecordDto dto) {
        if (dto == null || dto.getEnrollmentId() == null || dto.getEnrollmentId() <= 0) {
            throw new ValidationException("Enrollment ID is required for saving grade");
        }

        GradeRecord record = GradeRecord.builder()
                .id(dto.getId())
                .enrollmentId(dto.getEnrollmentId())
                .studentId(dto.getStudentId())
                .courseCode(dto.getCourseCode())
                .courseTitle(dto.getCourseTitle())
                .assignmentTitle(dto.getAssignmentTitle() != null ? dto.getAssignmentTitle() : "Assignment")
                .maxPoints(dto.getMaxPoints() != null ? dto.getMaxPoints() : 100.0)
                .pointsEarned(dto.getPointsEarned() != null ? dto.getPointsEarned() : 0.0)
                .letterGrade(dto.getLetterGrade() != null ? dto.getLetterGrade() : "A")
                .gradedDate(dto.getGradedDate() != null ? dto.getGradedDate() : LocalDate.now())
                .gradedBy(dto.getGradedBy() != null ? dto.getGradedBy() : "FACULTY")
                .comments(dto.getComments())
                .build();

        GradeRecord saved = gradeRecordRepository.save(record);
        return mapToGradeRecordDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CompletedCourseDto> getCompletedCourses(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new ValidationException("Valid student ID is required");
        }
        return completedCourseRepository.findByStudentIdOrderByYearDescSemesterDesc(studentId).stream()
                .map(this::mapToCompletedCourseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeRecordDto> getGradesForEnrollment(Long enrollmentId) {
        if (enrollmentId == null || enrollmentId <= 0) {
            throw new ValidationException("Valid enrollment ID is required");
        }
        return gradeRecordRepository.findByEnrollmentIdOrderByIdDesc(enrollmentId).stream()
                .map(this::mapToGradeRecordDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DegreeProgressDto getDegreeProgress(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new ValidationException("Valid student ID is required");
        }
        DegreeProgress dp = degreeProgressRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    DegreeProgress def = DegreeProgress.builder()
                            .studentId(studentId)
                            .programId(1L)
                            .totalCreditsRequired(120)
                            .totalCreditsCompleted(0)
                            .generalEdCompleted(0)
                            .majorCreditsCompleted(0)
                            .electiveCreditsCompleted(0)
                            .progressPercentage(0.0)
                            .build();
                    def.calculatePercentage();
                    return def;
                });
        return mapToDegreeProgressDto(dp);
    }

    @Transactional
    public DegreeProgressDto updateDegreeProgress(DegreeProgressDto dto) {
        if (dto == null || dto.getStudentId() == null || dto.getStudentId() <= 0) {
            throw new ValidationException("Student ID is required for degree progress");
        }

        DegreeProgress progress = degreeProgressRepository.findByStudentId(dto.getStudentId())
                .orElse(DegreeProgress.builder()
                        .studentId(dto.getStudentId())
                        .build());

        progress.setProgramId(dto.getProgramId() != null ? dto.getProgramId() : 1L);
        progress.setTotalCreditsRequired(dto.getTotalCreditsRequired() != null ? dto.getTotalCreditsRequired() : 120);
        progress.setTotalCreditsCompleted(dto.getTotalCreditsCompleted() != null ? dto.getTotalCreditsCompleted() : 0);
        progress.setGeneralEdCompleted(dto.getGeneralEdCompleted() != null ? dto.getGeneralEdCompleted() : 0);
        progress.setMajorCreditsCompleted(dto.getMajorCreditsCompleted() != null ? dto.getMajorCreditsCompleted() : 0);
        progress.setElectiveCreditsCompleted(dto.getElectiveCreditsCompleted() != null ? dto.getElectiveCreditsCompleted() : 0);
        progress.setExpectedGraduationDate(dto.getExpectedGraduationDate());
        progress.calculatePercentage();

        DegreeProgress saved = degreeProgressRepository.save(progress);
        return mapToDegreeProgressDto(saved);
    }

    @Transactional(readOnly = true)
    public AcademicRecordDto getAcademicRecord(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new ValidationException("Valid student ID is required");
        }

        CumulativeRecord cumulative = cumulativeRecordRepository.findByStudentId(studentId)
                .orElse(CumulativeRecord.builder()
                        .studentId(studentId)
                        .cumulativeGpa(0.0)
                        .totalCreditsEarned(0)
                        .totalCreditsAttempted(0)
                        .graduationStatus("IN_PROGRESS")
                        .build());

        List<CompletedCourseDto> completedCourses = getCompletedCourses(studentId);
        List<GradeRecordDto> gradeHistory = gradeRecordRepository.findByStudentIdOrderByIdDesc(studentId).stream()
                .map(this::mapToGradeRecordDto)
                .collect(Collectors.toList());
        DegreeProgressDto degreeProgress = getDegreeProgress(studentId);

        return AcademicRecordDto.builder()
                .studentId(studentId)
                .cumulativeGpa(cumulative.getCumulativeGpa())
                .totalCreditsEarned(cumulative.getTotalCreditsEarned())
                .totalCreditsAttempted(cumulative.getTotalCreditsAttempted())
                .graduationStatus(cumulative.getGraduationStatus())
                .completedCourses(completedCourses)
                .gradeHistory(gradeHistory)
                .degreeProgress(degreeProgress)
                .build();
    }

    private CompletedCourseDto mapToCompletedCourseDto(CompletedCourse entity) {
        return CompletedCourseDto.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .courseCode(entity.getCourseCode())
                .courseTitle(entity.getCourseTitle())
                .credits(entity.getCredits())
                .grade(entity.getGrade())
                .semester(entity.getSemester())
                .year(entity.getYear())
                .instructorName(entity.getInstructorName())
                .build();
    }

    private GradeRecordDto mapToGradeRecordDto(GradeRecord entity) {
        return GradeRecordDto.builder()
                .id(entity.getId())
                .enrollmentId(entity.getEnrollmentId())
                .studentId(entity.getStudentId())
                .courseCode(entity.getCourseCode())
                .courseTitle(entity.getCourseTitle())
                .assignmentTitle(entity.getAssignmentTitle())
                .maxPoints(entity.getMaxPoints())
                .pointsEarned(entity.getPointsEarned())
                .letterGrade(entity.getLetterGrade())
                .gradedDate(entity.getGradedDate())
                .gradedBy(entity.getGradedBy())
                .comments(entity.getComments())
                .build();
    }

    private DegreeProgressDto mapToDegreeProgressDto(DegreeProgress entity) {
        return DegreeProgressDto.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .programId(entity.getProgramId())
                .totalCreditsRequired(entity.getTotalCreditsRequired())
                .totalCreditsCompleted(entity.getTotalCreditsCompleted())
                .generalEdCompleted(entity.getGeneralEdCompleted())
                .majorCreditsCompleted(entity.getMajorCreditsCompleted())
                .electiveCreditsCompleted(entity.getElectiveCreditsCompleted())
                .progressPercentage(entity.getProgressPercentage())
                .expectedGraduationDate(entity.getExpectedGraduationDate())
                .build();
    }
}
