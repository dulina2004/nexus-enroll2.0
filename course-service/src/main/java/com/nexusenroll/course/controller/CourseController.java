package com.nexusenroll.course.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.course.dto.*;
import com.nexusenroll.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for course catalog management, change requests, and degree programs.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // =========================================================================
    // Course Catalog Endpoints
    // =========================================================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String departmentCode,
            @RequestParam(required = false) Integer courseNumber,
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) String instructorName,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer year) {

        String kw = search != null ? search : keyword;
        String dept = department != null ? department : departmentCode;

        CourseSearchFilterDTO filter = CourseSearchFilterDTO.builder()
                .keyword(kw)
                .departmentCode(dept)
                .courseNumber(courseNumber)
                .instructorId(instructorId)
                .instructorName(instructorName)
                .semester(semester)
                .year(year)
                .build();

        List<CourseResponseDTO> courses = courseService.getCourses(filter);
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> getCourse(@PathVariable Long id) {
        CourseResponseDTO course = courseService.getCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", course));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDTO>> createCourse(@Valid @RequestBody CourseRequestDTO request) {
        CourseResponseDTO created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Course created successfully", created));
    }

    @PutMapping("/{id:\\d+}/capacity")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> updateCapacity(
            @PathVariable Long id,
            @RequestBody(required = false) CourseUpdateCapacityDTO request) {
        Integer cap = request != null ? request.getCapacity() : null;
        CourseResponseDTO updated = courseService.updateCapacity(id, cap);
        return ResponseEntity.ok(ApiResponse.success("Course capacity updated successfully", updated));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequestDTO request) {
        CourseResponseDTO updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", updated));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> deleteCourse(@PathVariable Long id) {
        CourseResponseDTO deleted = courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course archived successfully", deleted));
    }

    // =========================================================================
    // Course Change Request Endpoints
    // =========================================================================

    @PostMapping("/change-requests")
    public ResponseEntity<ApiResponse<CourseChangeRequestResponseDTO>> submitChangeRequest(
            @RequestBody CourseChangeRequestCreateDTO request) {
        CourseChangeRequestResponseDTO created = courseService.submitChangeRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Course change request submitted successfully", created));
    }

    @GetMapping("/change-requests")
    public ResponseEntity<ApiResponse<List<CourseChangeRequestResponseDTO>>> getChangeRequests(
            @RequestParam(required = false) String status) {
        List<CourseChangeRequestResponseDTO> list = courseService.getChangeRequests(status);
        return ResponseEntity.ok(ApiResponse.success("Course change requests retrieved successfully", list));
    }

    @PutMapping("/change-requests/{id:\\d+}/approve")
    public ResponseEntity<ApiResponse<CourseChangeRequestResponseDTO>> approveChangeRequest(
            @PathVariable Long id,
            @RequestBody(required = false) CourseChangeRequestReviewDTO review) {
        Long adminUserId = review != null ? review.getAdminUserId() : null;
        String comment = review != null ? review.getComment() : null;
        CourseChangeRequestResponseDTO approved = courseService.approveChangeRequest(id, adminUserId, comment);
        return ResponseEntity.ok(ApiResponse.success("Course change request approved successfully", approved));
    }

    @PutMapping("/change-requests/{id:\\d+}/reject")
    public ResponseEntity<ApiResponse<CourseChangeRequestResponseDTO>> rejectChangeRequest(
            @PathVariable Long id,
            @RequestBody(required = false) CourseChangeRequestReviewDTO review) {
        Long adminUserId = review != null ? review.getAdminUserId() : null;
        String comment = review != null ? review.getComment() : null;
        CourseChangeRequestResponseDTO rejected = courseService.rejectChangeRequest(id, adminUserId, comment);
        return ResponseEntity.ok(ApiResponse.success("Course change request rejected successfully", rejected));
    }

    // =========================================================================
    // Degree Program Endpoints
    // =========================================================================

    @GetMapping({"/programs", "/degree-programs"})
    public ResponseEntity<ApiResponse<List<DegreeProgramResponseDTO>>> getDegreePrograms() {
        List<DegreeProgramResponseDTO> list = courseService.getDegreePrograms();
        return ResponseEntity.ok(ApiResponse.success("Degree programs retrieved successfully", list));
    }

    @GetMapping({"/programs/{id:\\d+}", "/degree-programs/{id:\\d+}"})
    public ResponseEntity<ApiResponse<DegreeProgramResponseDTO>> getDegreeProgramById(@PathVariable Long id) {
        DegreeProgramResponseDTO program = courseService.getDegreeProgramById(id);
        return ResponseEntity.ok(ApiResponse.success("Degree program retrieved successfully", program));
    }

    @PostMapping({"/programs", "/degree-programs"})
    public ResponseEntity<ApiResponse<DegreeProgramResponseDTO>> createDegreeProgram(
            @RequestBody DegreeProgramRequestDTO request) {
        DegreeProgramResponseDTO created = courseService.createDegreeProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Degree program created successfully", created));
    }

    @PutMapping({"/programs/{id:\\d+}", "/degree-programs/{id:\\d+}"})
    public ResponseEntity<ApiResponse<DegreeProgramResponseDTO>> updateDegreeProgram(
            @PathVariable Long id,
            @RequestBody DegreeProgramRequestDTO request) {
        DegreeProgramResponseDTO updated = courseService.updateDegreeProgram(id, request);
        return ResponseEntity.ok(ApiResponse.success("Degree program updated successfully", updated));
    }

    @DeleteMapping({"/programs/{id:\\d+}", "/degree-programs/{id:\\d+}"})
    public ResponseEntity<ApiResponse<Void>> deleteDegreeProgram(@PathVariable Long id) {
        courseService.deleteDegreeProgram(id);
        return ResponseEntity.ok(ApiResponse.success("Degree program deleted successfully", null));
    }

    @PostMapping({"/programs/{id:\\d+}/requirements", "/degree-programs/{id:\\d+}/requirements"})
    public ResponseEntity<ApiResponse<ProgramRequirementResponseDTO>> addProgramRequirement(
            @PathVariable Long id,
            @RequestBody ProgramRequirementRequestDTO request) {
        ProgramRequirementResponseDTO added = courseService.addProgramRequirement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Program requirement added successfully", added));
    }

    // =========================================================================
    // Department & Section Endpoints
    // =========================================================================

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDTO>>> getDepartments() {
        List<DepartmentResponseDTO> list = courseService.getDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", list));
    }

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<CourseSectionResponseDTO>>> getSections(
            @RequestParam(required = false) Long courseId) {
        List<CourseSectionResponseDTO> list = courseService.getSections(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course sections retrieved successfully", list));
    }
}
