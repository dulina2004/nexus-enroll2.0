package com.nexusenroll.student.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.student.dto.*;
import com.nexusenroll.student.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for student profile management, schedules, and academic progress.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getStudents(@RequestParam(required = false) Long userId) {
        if (userId != null && userId > 0) {
            StudentResponseDTO student = studentService.getStudentByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Student profile retrieved successfully", student));
        }
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student profile retrieved successfully", student));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO request) {
        StudentResponseDTO updated = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student profile updated successfully", updated));
    }

    @GetMapping("/{id:\\d+}/schedule")
    public ResponseEntity<ApiResponse<ScheduleDTO>> getSchedule(
            @PathVariable Long id,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer year) {
        ScheduleDTO schedule = studentService.getSchedule(id, semester, year);
        return ResponseEntity.ok(ApiResponse.success("Schedule retrieved successfully", schedule));
    }

    @GetMapping("/{id:\\d+}/schedule/history")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getScheduleHistory(@PathVariable Long id) {
        List<ScheduleDTO> history = studentService.getScheduleHistory(id);
        return ResponseEntity.ok(ApiResponse.success("Schedule history retrieved successfully", history));
    }

    @GetMapping("/{id:\\d+}/progress")
    public ResponseEntity<ApiResponse<DegreeProgressDTO>> getProgress(@PathVariable Long id) {
        DegreeProgressDTO progress = studentService.getDegreeProgress(id);
        return ResponseEntity.ok(ApiResponse.success("Degree progress retrieved successfully", progress));
    }
}
