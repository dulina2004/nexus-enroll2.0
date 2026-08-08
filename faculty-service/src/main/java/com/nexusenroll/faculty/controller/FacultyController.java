package com.nexusenroll.faculty.controller;

import com.nexusenroll.common.dto.ApiResponse;
import com.nexusenroll.faculty.dto.BatchGradeRequestDto;
import com.nexusenroll.faculty.dto.BatchGradeResultDto;
import com.nexusenroll.faculty.dto.FacultyDto;
import com.nexusenroll.faculty.dto.GradeDto;
import com.nexusenroll.faculty.dto.GradeOperationDto;
import com.nexusenroll.faculty.dto.RosterDto;
import com.nexusenroll.faculty.service.FacultyService;
import com.nexusenroll.faculty.service.GradeService;
import com.nexusenroll.faculty.state.GradeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final GradeService gradeService;

    @GetMapping("/roster")
    public ResponseEntity<ApiResponse<RosterDto>> getRoster(@RequestParam("sectionId") long sectionId) {
        RosterDto roster = facultyService.getClassRoster(sectionId);
        return ResponseEntity.ok(ApiResponse.success("Class roster retrieved successfully", roster));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyDto>> getFacultyById(@PathVariable("id") long id) {
        FacultyDto faculty = facultyService.getFaculty(id);
        return ResponseEntity.ok(ApiResponse.success("Faculty details retrieved successfully", faculty));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<FacultyDto>> getFacultyByUserId(@PathVariable("userId") long userId) {
        FacultyDto faculty = facultyService.getFacultyByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Faculty details retrieved successfully", faculty));
    }

    @PostMapping("/grades/draft")
    public ResponseEntity<ApiResponse<GradeDto>> saveDraft(@RequestBody GradeDto dto) {
        GradeContext context = mapToContext(dto);
        GradeContext saved = gradeService.createOrUpdateDraft(context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Grade draft saved successfully", mapToDto(saved)));
    }

    @PostMapping("/grades/submit")
    public ResponseEntity<ApiResponse<GradeDto>> submitGrade(@RequestBody GradeOperationDto dto) {
        if (dto.getGradeId() == null || dto.getGradeId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid gradeId is required"));
        }
        GradeContext submitted = gradeService.submitGrade(dto.getGradeId());
        return ResponseEntity.ok(ApiResponse.success("Grade submitted for approval (Status: PENDING)", mapToDto(submitted)));
    }

    @PostMapping("/grades/approve")
    public ResponseEntity<ApiResponse<GradeDto>> approveGrade(@RequestBody GradeOperationDto dto) {
        if (dto.getGradeId() == null || dto.getGradeId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid gradeId is required"));
        }
        GradeContext approved = gradeService.approveGrade(dto.getGradeId());
        return ResponseEntity.ok(ApiResponse.success("Grade approved & Academic Record Service notified (Status: APPROVED)", mapToDto(approved)));
    }

    @PostMapping("/grades/reject")
    public ResponseEntity<ApiResponse<GradeDto>> rejectGrade(@RequestBody GradeOperationDto dto) {
        if (dto.getGradeId() == null || dto.getGradeId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Valid gradeId is required"));
        }
        GradeContext rejected = gradeService.rejectGrade(dto.getGradeId());
        return ResponseEntity.ok(ApiResponse.success("Grade rejected back to draft (Status: DRAFT)", mapToDto(rejected)));
    }

    @PostMapping("/sections/{sectionId}/grades/batch")
    public ResponseEntity<ApiResponse<BatchGradeResultDto>> submitBatch(
            @PathVariable("sectionId") long sectionId,
            @RequestBody BatchGradeRequestDto requestDto) {

        List<GradeContext> gradeContexts = new ArrayList<>();
        if (requestDto.getGrades() != null) {
            for (BatchGradeRequestDto.GradeItemDto item : requestDto.getGrades()) {
                GradeContext g = new GradeContext();
                g.setEnrollmentId(item.getEnrollmentId() != null ? item.getEnrollmentId() : 0L);
                g.setStudentId(item.getStudentId() != null ? item.getStudentId() : 0L);
                g.setSectionId(sectionId);
                g.setLetterGrade(item.getEffectiveGrade());
                g.setAssignmentTitle(item.getAssignmentTitle() != null ? item.getAssignmentTitle() : "Final Grade");
                g.setPointsEarned(item.getPointsEarned() != null ? item.getPointsEarned() : 0.0);
                g.setMaxPoints(item.getMaxPoints() != null ? item.getMaxPoints() : 100.0);
                g.setGradedBy(item.getGradedBy() != null ? item.getGradedBy() : "FACULTY");
                gradeContexts.add(g);
            }
        }

        BatchGradeResultDto result = gradeService.submitBatch(sectionId, gradeContexts);
        return ResponseEntity.ok(ApiResponse.success("Batch grade submission completed", result));
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<GradeDto>>> getGradesByEnrollment(@RequestParam("enrollmentId") long enrollmentId) {
        List<GradeContext> grades = gradeService.getGradesByEnrollment(enrollmentId);
        List<GradeDto> dtos = grades.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved successfully", dtos));
    }

    @GetMapping("/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeDto>> getGradeById(@PathVariable("gradeId") long gradeId) {
        GradeContext grade = gradeService.getGrade(gradeId);
        return ResponseEntity.ok(ApiResponse.success("Grade details retrieved successfully", mapToDto(grade)));
    }

    private GradeContext mapToContext(GradeDto dto) {
        GradeContext context = new GradeContext();
        if (dto.getId() != null) {
            context.setId(dto.getId());
        }
        context.setEnrollmentId(dto.getEnrollmentId());
        context.setStudentId(dto.getStudentId());
        context.setSectionId(dto.getSectionId());
        context.setAssignmentTitle(dto.getAssignmentTitle() != null ? dto.getAssignmentTitle() : "Final Grade");
        context.setPointsEarned(dto.getPointsEarned());
        context.setMaxPoints(dto.getMaxPoints() > 0 ? dto.getMaxPoints() : 100.0);
        context.setLetterGrade(dto.getLetterGrade());
        context.setComments(dto.getComments());
        context.setGradedBy(dto.getGradedBy() != null ? dto.getGradedBy() : "FACULTY");
        return context;
    }

    private GradeDto mapToDto(GradeContext context) {
        return GradeDto.builder()
                .id(context.getId())
                .enrollmentId(context.getEnrollmentId())
                .studentId(context.getStudentId())
                .sectionId(context.getSectionId())
                .assignmentTitle(context.getAssignmentTitle())
                .pointsEarned(context.getPointsEarned())
                .maxPoints(context.getMaxPoints())
                .letterGrade(context.getLetterGrade())
                .comments(context.getComments())
                .gradedBy(context.getGradedBy())
                .status(context.getStatusName())
                .canEdit(context.canEdit())
                .build();
    }
}
