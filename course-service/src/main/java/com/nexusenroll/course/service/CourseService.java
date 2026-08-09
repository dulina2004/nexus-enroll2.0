package com.nexusenroll.course.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.common.util.StringUtil;
import com.nexusenroll.course.dto.*;
import com.nexusenroll.course.model.*;
import com.nexusenroll.course.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service handling all course, course change request, degree program, department, and section operations.
 */
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseChangeRequestRepository changeRequestRepository;
    private final DegreeProgramRepository degreeProgramRepository;
    private final ProgramRequirementRepository programRequirementRepository;

    public CourseService(CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         CourseSectionRepository courseSectionRepository,
                         CourseChangeRequestRepository changeRequestRepository,
                         DegreeProgramRepository degreeProgramRepository,
                         ProgramRequirementRepository programRequirementRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.courseSectionRepository = courseSectionRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.degreeProgramRepository = degreeProgramRepository;
        this.programRequirementRepository = programRequirementRepository;
    }

    // =========================================================================
    // Course Operations
    // =========================================================================

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCourses(CourseSearchFilterDTO filter) {
        if (filter == null) {
            filter = new CourseSearchFilterDTO();
        }
        List<Course> courses = courseRepository.findAll(CourseSpecification.filter(filter));
        return courses.stream().map(this::mapToCourseResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO getCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
        return mapToCourseResponseDTO(course);
    }

    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        validateCourseRequest(request, true);

        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new ValidationException("Department not found: " + request.getDepartmentId());
        }

        if (courseRepository.findByCourseCode(request.getCourseCode()).isPresent()) {
            throw new ValidationException("Course code already exists: " + request.getCourseCode());
        }

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseNumber(request.getCourseNumber())
                .title(request.getTitle())
                .description(request.getDescription())
                .credits(request.getCredits())
                .capacity(request.getCapacity() == null ? 0 : request.getCapacity())
                .departmentId(request.getDepartmentId())
                .level(StringUtil.isNotEmpty(request.getLevel()) ? request.getLevel() : "100")
                .prerequisites(normalizeStringOrList(request.getPrerequisites()))
                .coRequisites(normalizeStringOrList(request.getCoRequisites()))
                .status(StringUtil.isNotEmpty(request.getStatus()) ? request.getStatus() : "ACTIVE")
                .build();

        Course saved = courseRepository.save(course);
        return mapToCourseResponseDTO(saved);
    }

    public CourseResponseDTO updateCapacity(Long id, Integer capacity) {
        if (capacity == null || capacity < 0) {
            throw new ValidationException("Capacity must be zero or greater");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        course.setCapacity(capacity);
        Course updated = courseRepository.save(course);
        return mapToCourseResponseDTO(updated);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        if (id == null || id <= 0 || request == null) {
            throw new ValidationException("Course ID and course payload are required");
        }
        validateCourseRequest(request, false);

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        if (request.getDepartmentId() != null && request.getDepartmentId() > 0) {
            if (!departmentRepository.existsById(request.getDepartmentId())) {
                throw new ValidationException("Department not found: " + request.getDepartmentId());
            }
            existing.setDepartmentId(request.getDepartmentId());
        }

        if (StringUtil.isNotEmpty(request.getCourseCode())) existing.setCourseCode(request.getCourseCode());
        if (request.getCourseNumber() != null) existing.setCourseNumber(request.getCourseNumber());
        if (StringUtil.isNotEmpty(request.getTitle())) existing.setTitle(request.getTitle());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getCredits() != null) existing.setCredits(request.getCredits());
        if (request.getCapacity() != null) existing.setCapacity(request.getCapacity());
        if (StringUtil.isNotEmpty(request.getLevel())) existing.setLevel(request.getLevel());
        if (request.getPrerequisites() != null) existing.setPrerequisites(normalizeStringOrList(request.getPrerequisites()));
        if (request.getCoRequisites() != null) existing.setCoRequisites(normalizeStringOrList(request.getCoRequisites()));
        if (StringUtil.isNotEmpty(request.getStatus())) existing.setStatus(request.getStatus());

        Course updated = courseRepository.save(existing);
        return mapToCourseResponseDTO(updated);
    }

    public CourseResponseDTO deleteCourse(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid course ID: " + id);
        }

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        int activeEnrolled = courseSectionRepository.sumActiveEnrolledCountByCourseId(id);
        if (activeEnrolled > 0) {
            throw new ValidationException("Cannot delete course: course has active section enrollments");
        }

        existing.setStatus("ARCHIVED");
        Course archived = courseRepository.save(existing);
        return mapToCourseResponseDTO(archived);
    }

    // =========================================================================
    // Course Change Requests
    // =========================================================================

    public CourseChangeRequestResponseDTO submitChangeRequest(CourseChangeRequestCreateDTO dto) {
        if (dto == null || dto.getCourseId() == null) {
            throw new ValidationException("Course ID and RequestedBy ID are required for change request");
        }
        Long requestedBy = dto.getRequestedBy() != null ? dto.getRequestedBy() : dto.getFacultyId();
        if (requestedBy == null || requestedBy <= 0) {
            throw new ValidationException("Course ID and RequestedBy ID are required for change request");
        }

        String proposedVal = StringUtil.isNotEmpty(dto.getProposedValue()) ? dto.getProposedValue() : dto.getProposedChanges();
        if (StringUtil.isEmpty(proposedVal)) {
            throw new ValidationException("Proposed value is required");
        }

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + dto.getCourseId()));

        String reqType = StringUtil.isNotEmpty(dto.getRequestType()) ? dto.getRequestType().toUpperCase() : "CAPACITY";
        String currentVal = dto.getCurrentValue();
        if (StringUtil.isEmpty(currentVal)) {
            if ("CAPACITY".equalsIgnoreCase(reqType)) {
                currentVal = String.valueOf(course.getCapacity());
            } else if ("DESCRIPTION".equalsIgnoreCase(reqType)) {
                currentVal = course.getDescription();
            } else if ("PREREQUISITE".equalsIgnoreCase(reqType)) {
                currentVal = course.getPrerequisites();
            }
        }

        String justification = StringUtil.isNotEmpty(dto.getJustification()) ? dto.getJustification() : dto.getReason();

        CourseChangeRequest request = CourseChangeRequest.builder()
                .courseId(dto.getCourseId())
                .sectionId(dto.getSectionId())
                .requestedBy(requestedBy)
                .requestType(reqType)
                .currentValue(currentVal)
                .proposedValue(proposedVal)
                .justification(justification)
                .status("PENDING")
                .build();

        CourseChangeRequest saved = changeRequestRepository.save(request);
        return mapToChangeRequestResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseChangeRequestResponseDTO> getChangeRequests(String status) {
        List<CourseChangeRequest> list;
        if (StringUtil.isNotEmpty(status)) {
            list = changeRequestRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
        } else {
            list = changeRequestRepository.findAllByOrderByCreatedAtDesc();
        }
        return list.stream().map(this::mapToChangeRequestResponseDTO).collect(Collectors.toList());
    }

    public CourseChangeRequestResponseDTO approveChangeRequest(Long requestId, Long adminUserId, String comment) {
        CourseChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found: " + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new ValidationException("Only PENDING change requests can be approved");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));

        String type = request.getRequestType();
        if ("CAPACITY".equalsIgnoreCase(type)) {
            int newCap = Integer.parseInt(request.getProposedValue().trim());
            course.setCapacity(newCap);
        } else if ("DESCRIPTION".equalsIgnoreCase(type)) {
            course.setDescription(request.getProposedValue());
        } else if ("PREREQUISITE".equalsIgnoreCase(type)) {
            course.setPrerequisites(request.getProposedValue());
        } else {
            try {
                int newCap = Integer.parseInt(request.getProposedValue().trim());
                course.setCapacity(newCap);
            } catch (Exception ignored) {}
        }
        courseRepository.save(course);

        request.setStatus("APPROVED");
        request.setReviewedBy(adminUserId);
        request.setReviewComment(comment);
        request.setReviewedAt(Instant.now());

        CourseChangeRequest approved = changeRequestRepository.save(request);
        return mapToChangeRequestResponseDTO(approved);
    }

    public CourseChangeRequestResponseDTO rejectChangeRequest(Long requestId, Long adminUserId, String comment) {
        CourseChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found: " + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new ValidationException("Only PENDING change requests can be rejected");
        }

        request.setStatus("REJECTED");
        request.setReviewedBy(adminUserId);
        request.setReviewComment(comment);
        request.setReviewedAt(Instant.now());

        CourseChangeRequest rejected = changeRequestRepository.save(request);
        return mapToChangeRequestResponseDTO(rejected);
    }

    // =========================================================================
    // Degree Programs
    // =========================================================================

    @Transactional(readOnly = true)
    public List<DegreeProgramResponseDTO> getDegreePrograms() {
        return degreeProgramRepository.findAll().stream()
                .map(this::mapToDegreeProgramResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DegreeProgramResponseDTO getDegreeProgramById(Long id) {
        DegreeProgram program = degreeProgramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Degree program not found: " + id));
        return mapToDegreeProgramResponseDTO(program);
    }

    public DegreeProgramResponseDTO createDegreeProgram(DegreeProgramRequestDTO request) {
        String code = StringUtil.isNotEmpty(request.getCode()) ? request.getCode() : request.getProgramCode();
        if (StringUtil.isEmpty(code) || StringUtil.isEmpty(request.getName())) {
            throw new ValidationException("Program code and name are required");
        }
        if (degreeProgramRepository.findByCode(code).isPresent()) {
            throw new ValidationException("Degree program code already exists: " + code);
        }

        Integer credits = request.getTotalCreditsRequired() != null ? request.getTotalCreditsRequired() : request.getRequiredCredits();

        DegreeProgram program = DegreeProgram.builder()
                .code(code)
                .name(request.getName())
                .departmentId(request.getDepartmentId())
                .totalCreditsRequired(credits != null ? credits : 120)
                .status(StringUtil.isNotEmpty(request.getStatus()) ? request.getStatus() : "ACTIVE")
                .build();

        DegreeProgram saved = degreeProgramRepository.save(program);
        return mapToDegreeProgramResponseDTO(saved);
    }

    public DegreeProgramResponseDTO updateDegreeProgram(Long id, DegreeProgramRequestDTO request) {
        if (id == null || id <= 0 || request == null) {
            throw new ValidationException("Degree program ID and payload are required");
        }
        DegreeProgram existing = degreeProgramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Degree program not found: " + id));

        String code = StringUtil.isNotEmpty(request.getCode()) ? request.getCode() : request.getProgramCode();
        if (StringUtil.isNotEmpty(code)) existing.setCode(code);
        if (StringUtil.isNotEmpty(request.getName())) existing.setName(request.getName());
        if (request.getDepartmentId() != null) existing.setDepartmentId(request.getDepartmentId());

        Integer credits = request.getTotalCreditsRequired() != null ? request.getTotalCreditsRequired() : request.getRequiredCredits();
        if (credits != null) existing.setTotalCreditsRequired(credits);

        if (StringUtil.isNotEmpty(request.getStatus())) existing.setStatus(request.getStatus());

        DegreeProgram updated = degreeProgramRepository.save(existing);
        return mapToDegreeProgramResponseDTO(updated);
    }

    public void deleteDegreeProgram(Long id) {
        DegreeProgram program = degreeProgramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Degree program not found: " + id));
        degreeProgramRepository.delete(program);
    }

    public ProgramRequirementResponseDTO addProgramRequirement(Long programId, ProgramRequirementRequestDTO request) {
        if (programId == null || programId <= 0 || request == null || request.getCourseId() == null) {
            throw new ValidationException("Program ID and Course ID are required");
        }

        DegreeProgram program = degreeProgramRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree program not found: " + programId));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));

        ProgramRequirement req = ProgramRequirement.builder()
                .programId(program.getId())
                .courseId(course.getId())
                .requirementType(StringUtil.isNotEmpty(request.getRequirementType()) ? request.getRequirementType() : "CORE")
                .minimumGrade(StringUtil.isNotEmpty(request.getMinimumGrade()) ? request.getMinimumGrade() : "C")
                .build();

        ProgramRequirement saved = programRequirementRepository.save(req);
        return mapToProgramRequirementResponseDTO(saved);
    }

    // =========================================================================
    // Departments & Sections
    // =========================================================================

    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getDepartments() {
        return departmentRepository.findAll().stream()
                .map(d -> DepartmentResponseDTO.builder()
                        .id(d.getId())
                        .code(d.getCode())
                        .name(d.getName())
                        .description(d.getDescription())
                        .headFacultyId(d.getHeadFacultyId())
                        .createdAt(d.getCreatedAt())
                        .updatedAt(d.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseSectionResponseDTO> getSections(Long courseId) {
        List<CourseSection> sections;
        if (courseId != null && courseId > 0) {
            sections = courseSectionRepository.findByCourseId(courseId);
        } else {
            sections = courseSectionRepository.findAll();
        }

        return sections.stream()
                .map(s -> CourseSectionResponseDTO.builder()
                        .id(s.getId())
                        .courseId(s.getCourseId())
                        .sectionNumber(s.getSectionNumber())
                        .instructorId(s.getInstructorId())
                        .semester(s.getSemester())
                        .year(s.getYear())
                        .scheduleDays(s.getScheduleDays())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .location(s.getLocation())
                        .capacity(s.getCapacity())
                        .enrolledCount(s.getEnrolledCount())
                        .status(s.getStatus())
                        .createdAt(s.getCreatedAt())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Private Helpers & Mapping
    // =========================================================================

    private void validateCourseRequest(CourseRequestDTO course, boolean isCreate) {
        if (course == null) {
            throw new ValidationException("Course payload is required");
        }
        if (isCreate) {
            if (StringUtil.isEmpty(course.getCourseCode())) {
                throw new ValidationException("Course code is required");
            }
            if (course.getCourseNumber() == null || course.getCourseNumber() <= 0) {
                throw new ValidationException("Course number must be greater than zero");
            }
            if (StringUtil.isEmpty(course.getTitle())) {
                throw new ValidationException("Course title is required");
            }
            if (course.getCredits() == null || course.getCredits() <= 0) {
                throw new ValidationException("Credits must be greater than zero");
            }
            if (course.getDepartmentId() == null || course.getDepartmentId() <= 0) {
                throw new ValidationException("Department ID is required");
            }
        }
        if (course.getCapacity() != null && course.getCapacity() < 0) {
            throw new ValidationException("Capacity must be zero or greater");
        }
    }

    private CourseResponseDTO mapToCourseResponseDTO(Course course) {
        String deptCode = null;
        String deptName = null;
        if (course.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(course.getDepartmentId()).orElse(null);
            if (dept != null) {
                deptCode = dept.getCode();
                deptName = dept.getName();
            }
        }

        return CourseResponseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseNumber(course.getCourseNumber())
                .title(course.getTitle())
                .description(course.getDescription())
                .credits(course.getCredits())
                .capacity(course.getCapacity())
                .departmentId(course.getDepartmentId())
                .departmentCode(deptCode)
                .departmentName(deptName)
                .level(course.getLevel())
                .status(course.getStatus())
                .prerequisites(course.getPrerequisites())
                .coRequisites(course.getCoRequisites())
                .prerequisiteDetails(parsePrerequisites(course.getCourseCode(), course.getPrerequisites(), false))
                .coRequisiteDetails(parsePrerequisites(course.getCourseCode(), course.getCoRequisites(), true))
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private CourseChangeRequestResponseDTO mapToChangeRequestResponseDTO(CourseChangeRequest req) {
        return CourseChangeRequestResponseDTO.builder()
                .id(req.getId())
                .courseId(req.getCourseId())
                .sectionId(req.getSectionId())
                .requestedBy(req.getRequestedBy())
                .facultyId(req.getRequestedBy())
                .requestType(req.getRequestType())
                .currentValue(req.getCurrentValue())
                .proposedValue(req.getProposedValue())
                .proposedChanges(req.getProposedValue())
                .justification(req.getJustification())
                .reason(req.getJustification())
                .status(req.getStatus())
                .reviewedBy(req.getReviewedBy())
                .reviewedAt(req.getReviewedAt())
                .reviewComment(req.getReviewComment())
                .createdAt(req.getCreatedAt())
                .build();
    }

    private DegreeProgramResponseDTO mapToDegreeProgramResponseDTO(DegreeProgram program) {
        List<ProgramRequirementResponseDTO> reqs = programRequirementRepository.findByProgramId(program.getId())
                .stream()
                .map(this::mapToProgramRequirementResponseDTO)
                .collect(Collectors.toList());

        return DegreeProgramResponseDTO.builder()
                .id(program.getId())
                .code(program.getCode())
                .programCode(program.getCode())
                .name(program.getName())
                .departmentId(program.getDepartmentId())
                .totalCreditsRequired(program.getTotalCreditsRequired())
                .requiredCredits(program.getTotalCreditsRequired())
                .status(program.getStatus())
                .createdAt(program.getCreatedAt())
                .requirements(reqs)
                .build();
    }

    private ProgramRequirementResponseDTO mapToProgramRequirementResponseDTO(ProgramRequirement req) {
        String courseCode = null;
        String courseTitle = null;
        if (req.getCourseId() != null) {
            Course c = courseRepository.findById(req.getCourseId()).orElse(null);
            if (c != null) {
                courseCode = c.getCourseCode();
                courseTitle = c.getTitle();
            }
        }
        return ProgramRequirementResponseDTO.builder()
                .id(req.getId())
                .programId(req.getProgramId())
                .courseId(req.getCourseId())
                .courseCode(courseCode)
                .courseTitle(courseTitle)
                .requirementType(req.getRequirementType())
                .minimumGrade(req.getMinimumGrade())
                .createdAt(req.getCreatedAt())
                .build();
    }

    private List<PrerequisiteDTO> parsePrerequisites(String courseCode, String value, boolean corequisite) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        List<PrerequisiteDTO> list = new ArrayList<>();
        for (String token : value.split(",")) {
            String code = token.trim();
            if (!code.isEmpty()) {
                list.add(new PrerequisiteDTO(courseCode, code, corequisite));
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private String normalizeStringOrList(Object input) {
        if (input == null) return null;
        if (input instanceof String s) {
            return s.trim().isEmpty() ? null : s.trim();
        }
        if (input instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(", "));
        }
        return input.toString().trim();
    }

    @Transactional(readOnly = true)
    public CourseSectionResponseDTO getSectionById(Long sectionId) {
        CourseSection s = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Course section not found: " + sectionId));
        return CourseSectionResponseDTO.builder()
                .id(s.getId())
                .courseId(s.getCourseId())
                .sectionNumber(s.getSectionNumber())
                .instructorId(s.getInstructorId())
                .semester(s.getSemester())
                .year(s.getYear())
                .scheduleDays(s.getScheduleDays())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .location(s.getLocation())
                .capacity(s.getCapacity())
                .enrolledCount(s.getEnrolledCount())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    @Transactional
    public void reserveSeat(Long sectionId) {
        CourseSection s = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Course section not found: " + sectionId));
        s.setEnrolledCount(s.getEnrolledCount() + 1);
        courseSectionRepository.save(s);
    }

    @Transactional
    public void releaseSeat(Long sectionId) {
        CourseSection s = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Course section not found: " + sectionId));
        if (s.getEnrolledCount() > 0) {
            s.setEnrolledCount(s.getEnrolledCount() - 1);
            courseSectionRepository.save(s);
        }
    }
}
