package com.nexusenroll.academicrecord.controller;

import com.nexusenroll.academicrecord.dto.AcademicRecordDto;
import com.nexusenroll.academicrecord.dto.CompletedCourseDto;
import com.nexusenroll.academicrecord.dto.DegreeProgressDto;
import com.nexusenroll.academicrecord.dto.GradeRecordDto;
import com.nexusenroll.academicrecord.service.AcademicRecordService;
import com.nexusenroll.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;

    @PostMapping("/completed-courses")
    public ResponseEntity<ApiResponse<CompletedCourseDto>> addCompletedCourse(
            @RequestParam(name = "studentId", required = false) Long paramStudentId,
            @Valid @RequestBody CompletedCourseDto dto) {

        Long studentId = dto.getStudentId() != null ? dto.getStudentId() : paramStudentId;
        if (studentId == null || studentId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid studentId is required"));
        }

        CompletedCourseDto created = academicRecordService.addCompletedCourse(studentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Completed course added successfully", created));
    }

    @GetMapping("/completed-courses")
    public ResponseEntity<ApiResponse<List<CompletedCourseDto>>> getCompletedCourses(
            @RequestParam("studentId") Long studentId) {
        if (studentId == null || studentId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Query parameter studentId is required"));
        }
        List<CompletedCourseDto> list = academicRecordService.getCompletedCourses(studentId);
        return ResponseEntity.ok(ApiResponse.success("Completed courses retrieved successfully", list));
    }

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<GradeRecordDto>> saveGrade(@Valid @RequestBody GradeRecordDto dto) {
        if (dto.getEnrollmentId() == null || dto.getEnrollmentId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid enrollmentId is required"));
        }
        GradeRecordDto saved = academicRecordService.saveGrade(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Grade recorded successfully", saved));
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<GradeRecordDto>>> getGrades(
            @RequestParam("enrollmentId") Long enrollmentId) {
        if (enrollmentId == null || enrollmentId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Query parameter enrollmentId is required"));
        }
        List<GradeRecordDto> list = academicRecordService.getGradesForEnrollment(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved successfully", list));
    }

    @GetMapping("/degree-progress")
    public ResponseEntity<ApiResponse<DegreeProgressDto>> getDegreeProgress(
            @RequestParam("studentId") Long studentId) {
        if (studentId == null || studentId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Query parameter studentId is required"));
        }
        DegreeProgressDto progress = academicRecordService.getDegreeProgress(studentId);
        return ResponseEntity.ok(ApiResponse.success("Degree progress retrieved successfully", progress));
    }

    @PostMapping("/degree-progress")
    public ResponseEntity<ApiResponse<DegreeProgressDto>> updateDegreeProgress(
            @Valid @RequestBody DegreeProgressDto dto) {
        if (dto.getStudentId() == null || dto.getStudentId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid studentId is required"));
        }
        DegreeProgressDto updated = academicRecordService.updateDegreeProgress(dto);
        return ResponseEntity.ok(ApiResponse.success("Degree progress updated successfully", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AcademicRecordDto>> getAcademicRecord(
            @RequestParam("studentId") Long studentId) {
        if (studentId == null || studentId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Query parameter studentId is required"));
        }
        AcademicRecordDto record = academicRecordService.getAcademicRecord(studentId);
        return ResponseEntity.ok(ApiResponse.success("Academic record retrieved successfully", record));
    }
}
