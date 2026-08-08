package com.nexusenroll.student.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.common.util.StringUtil;
import com.nexusenroll.student.dto.*;
import com.nexusenroll.student.model.Student;
import com.nexusenroll.student.repository.StudentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service handling student profile management, schedule retrieval, and progress tracking.
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseServiceClient courseServiceClient;
    private final JdbcTemplate jdbcTemplate;

    public StudentService(StudentRepository studentRepository,
                          CourseServiceClient courseServiceClient,
                          JdbcTemplate jdbcTemplate) {
        this.studentRepository = studentRepository;
        this.courseServiceClient = courseServiceClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudent(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new ValidationException("Invalid student ID: " + studentId);
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        return mapToResponseDTO(student);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + userId));
        return mapToResponseDTO(student);
    }

    public StudentResponseDTO updateStudent(Long studentId, StudentRequestDTO request) {
        if (studentId == null || studentId <= 0 || request == null) {
            throw new ValidationException("Student ID and student data are required");
        }

        Student existing = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        if (request.getDateOfBirth() != null) existing.setDateOfBirth(request.getDateOfBirth());
        if (StringUtil.isNotEmpty(request.getGender())) existing.setGender(request.getGender());
        if (StringUtil.isNotEmpty(request.getAddress())) existing.setAddress(request.getAddress());
        if (StringUtil.isNotEmpty(request.getCity())) existing.setCity(request.getCity());
        if (StringUtil.isNotEmpty(request.getState())) existing.setState(request.getState());
        if (StringUtil.isNotEmpty(request.getPostalCode())) existing.setPostalCode(request.getPostalCode());
        if (StringUtil.isNotEmpty(request.getCountry())) existing.setCountry(request.getCountry());
        if (StringUtil.isNotEmpty(request.getEmergencyContactName())) existing.setEmergencyContactName(request.getEmergencyContactName());
        if (StringUtil.isNotEmpty(request.getEmergencyContactPhone())) existing.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getGraduationDate() != null) existing.setGraduationDate(request.getGraduationDate());
        if (StringUtil.isNotEmpty(request.getStatus())) existing.setStatus(request.getStatus());

        Student updated = studentRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Transactional(readOnly = true)
    public ScheduleDTO getSchedule(Long studentId, String semester, Integer year) {
        getStudent(studentId); // Ensures student exists

        List<EnrollmentRecord> enrollments = fetchEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            return ScheduleDTO.builder()
                    .studentId(studentId)
                    .semester(semester != null ? semester : "")
                    .year(year != null ? year : 0)
                    .items(Collections.emptyList())
                    .build();
        }

        List<Long> sectionIds = enrollments.stream().map(e -> e.sectionId).toList();
        Map<Long, SectionDetailsDTO> detailsMap = courseServiceClient.getSectionDetailsMap(sectionIds);

        List<ScheduleItemDTO> items = new ArrayList<>();
        for (EnrollmentRecord enc : enrollments) {
            SectionDetailsDTO sec = detailsMap.get(enc.sectionId);
            if (sec == null) continue;

            if (semester != null && !semester.isBlank() && !semester.equalsIgnoreCase(sec.getSemester())) {
                continue;
            }
            if (year != null && year > 0 && !year.equals(sec.getYear())) {
                continue;
            }

            items.add(ScheduleItemDTO.builder()
                    .sectionId(sec.getSectionId())
                    .courseCode(sec.getCourseCode())
                    .courseTitle(sec.getCourseTitle())
                    .scheduleDays(sec.getScheduleDays())
                    .startTime(sec.getStartTime())
                    .endTime(sec.getEndTime())
                    .location(sec.getLocation())
                    .status(enc.status)
                    .build());
        }

        return ScheduleDTO.builder()
                .studentId(studentId)
                .semester(semester != null ? semester : "")
                .year(year != null ? year : 0)
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleHistory(Long studentId) {
        getStudent(studentId);

        List<EnrollmentRecord> enrollments = fetchEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> sectionIds = enrollments.stream().map(e -> e.sectionId).toList();
        Map<Long, SectionDetailsDTO> detailsMap = courseServiceClient.getSectionDetailsMap(sectionIds);

        Map<String, ScheduleDTO> scheduleMap = new LinkedHashMap<>();

        for (EnrollmentRecord enc : enrollments) {
            SectionDetailsDTO sec = detailsMap.get(enc.sectionId);
            if (sec == null) continue;

            String key = sec.getSemester() + "-" + sec.getYear();
            ScheduleDTO sched = scheduleMap.computeIfAbsent(key, k -> ScheduleDTO.builder()
                    .studentId(studentId)
                    .semester(sec.getSemester())
                    .year(sec.getYear())
                    .items(new ArrayList<>())
                    .build());

            sched.getItems().add(ScheduleItemDTO.builder()
                    .sectionId(sec.getSectionId())
                    .courseCode(sec.getCourseCode())
                    .courseTitle(sec.getCourseTitle())
                    .scheduleDays(sec.getScheduleDays())
                    .startTime(sec.getStartTime())
                    .endTime(sec.getEndTime())
                    .location(sec.getLocation())
                    .status(enc.status)
                    .build());
        }

        return new ArrayList<>(scheduleMap.values());
    }

    @Transactional(readOnly = true)
    public DegreeProgressDTO getDegreeProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        return DegreeProgressDTO.builder()
                .studentId(studentId)
                .completedCredits(student.getTotalCreditsEarned() != null ? student.getTotalCreditsEarned() : 0)
                .requiredCredits(120)
                .gpa(student.getGpa() != null ? student.getGpa().doubleValue() : 0.0)
                .completedCourses(Collections.emptyList())
                .remainingRequirements(Collections.emptyList())
                .build();
    }

    private List<EnrollmentRecord> fetchEnrollmentsForStudent(Long studentId) {
        String sql = "SELECT section_id, status FROM nexus_enrollment.enrollments WHERE student_id = ?";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new EnrollmentRecord(rs.getLong("section_id"), rs.getString("status")), studentId);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private StudentResponseDTO mapToResponseDTO(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .userId(student.getUserId())
                .studentId(student.getStudentId())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .address(student.getAddress())
                .city(student.getCity())
                .state(student.getState())
                .postalCode(student.getPostalCode())
                .country(student.getCountry())
                .emergencyContactName(student.getEmergencyContactName())
                .emergencyContactPhone(student.getEmergencyContactPhone())
                .enrollmentDate(student.getEnrollmentDate())
                .graduationDate(student.getGraduationDate())
                .status(student.getStatus())
                .gpa(student.getGpa())
                .totalCreditsEarned(student.getTotalCreditsEarned())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    private record EnrollmentRecord(Long sectionId, String status) {}
}
