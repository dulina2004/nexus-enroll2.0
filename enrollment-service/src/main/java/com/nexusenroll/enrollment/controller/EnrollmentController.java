package com.nexusenroll.enrollment.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.enrollment.dto.EnrollmentOverrideRequestDTO;
import com.nexusenroll.enrollment.dto.EnrollmentRequestDTO;
import com.nexusenroll.enrollment.dto.WaitlistRequestDTO;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import com.nexusenroll.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring Boot REST Controller for student enrollment and waitlist management.
 */
@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Enrollment>> createEnrollment(@Valid @RequestBody EnrollmentRequestDTO request) {
        Enrollment enrollment = enrollmentService.enroll(request.getStudentId(), request.getSectionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Enrollment created successfully", enrollment));
    }

    @PostMapping("/override")
    public ResponseEntity<ApiResponse<Enrollment>> overrideEnrollment(@Valid @RequestBody EnrollmentOverrideRequestDTO request) {
        Enrollment enrollment = enrollmentService.enrollWithOverride(
                request.getStudentId(),
                request.getSectionId(),
                request.getAdminUserId(),
                request.getReason()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Enrollment override completed successfully", enrollment));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Enrollment>> dropEnrollment(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.drop(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment dropped successfully", enrollment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Enrollment>>> getEnrollmentsForStudent(@RequestParam("studentId") Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments retrieved successfully", enrollments));
    }

    @PostMapping("/waitlist")
    public ResponseEntity<ApiResponse<WaitlistEntry>> joinWaitlist(@Valid @RequestBody WaitlistRequestDTO request) {
        WaitlistEntry entry = enrollmentService.joinWaitlist(request.getStudentId(), request.getSectionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Added to waitlist successfully", entry));
    }

    @DeleteMapping("/waitlist/{id:\\d+}")
    public ResponseEntity<ApiResponse<Void>> cancelWaitlist(@PathVariable Long id) {
        enrollmentService.cancelWaitlist(id);
        return ResponseEntity.ok(ApiResponse.success("Waitlist entry cancelled successfully", null));
    }

    @GetMapping("/waitlist")
    public ResponseEntity<ApiResponse<List<WaitlistEntry>>> getWaitlistForStudent(@RequestParam("studentId") Long studentId) {
        List<WaitlistEntry> waitlist = enrollmentService.getWaitlistForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success("Waitlist entries retrieved successfully", waitlist));
    }
}
