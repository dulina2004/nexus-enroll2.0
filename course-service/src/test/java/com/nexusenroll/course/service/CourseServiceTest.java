package com.nexusenroll.course.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.course.dto.*;
import com.nexusenroll.course.model.*;
import com.nexusenroll.course.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CourseSectionRepository courseSectionRepository;
    @Mock
    private CourseChangeRequestRepository changeRequestRepository;
    @Mock
    private DegreeProgramRepository degreeProgramRepository;
    @Mock
    private ProgramRequirementRepository programRequirementRepository;

    @InjectMocks
    private CourseService courseService;

    private Department department;
    private Course course;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .code("CS")
                .name("Computer Science")
                .build();

        course = Course.builder()
                .id(100L)
                .courseCode("CS101")
                .courseNumber(101)
                .title("Intro to Computer Science")
                .description("Basics of CS")
                .credits(3)
                .capacity(30)
                .departmentId(1L)
                .level("100")
                .status("ACTIVE")
                .prerequisites("CS100")
                .build();
    }

    @Test
    void getCourse_existingId_returnsCourseResponseDTO() {
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        CourseResponseDTO result = courseService.getCourse(100L);

        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        assertEquals("Computer Science", result.getDepartmentName());
        assertEquals(1, result.getPrerequisiteDetails().size());
    }

    @Test
    void getCourse_nonExistingId_throwsResourceNotFoundException() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourse(999L));
    }

    @Test
    void createCourse_validRequest_returnsCreatedCourse() {
        CourseRequestDTO request = CourseRequestDTO.builder()
                .courseCode("CS102")
                .courseNumber(102)
                .title("Data Structures")
                .credits(4)
                .capacity(25)
                .departmentId(1L)
                .build();

        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findByCourseCode("CS102")).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course c = invocation.getArgument(0);
            c.setId(101L);
            return c;
        });

        CourseResponseDTO result = courseService.createCourse(request);

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("CS102", result.getCourseCode());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_invalidDepartment_throwsValidationException() {
        CourseRequestDTO request = CourseRequestDTO.builder()
                .courseCode("CS102")
                .courseNumber(102)
                .title("Data Structures")
                .credits(4)
                .departmentId(99L)
                .build();

        when(departmentRepository.existsById(99L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> courseService.createCourse(request));
    }

    @Test
    void updateCapacity_negativeCapacity_throwsValidationException() {
        assertThrows(ValidationException.class, () -> courseService.updateCapacity(100L, -5));
    }

    @Test
    void deleteCourse_withActiveEnrollments_throwsValidationException() {
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(courseSectionRepository.sumActiveEnrolledCountByCourseId(100L)).thenReturn(10);

        assertThrows(ValidationException.class, () -> courseService.deleteCourse(100L));
    }

    @Test
    void deleteCourse_noActiveEnrollments_archivesCourse() {
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(courseSectionRepository.sumActiveEnrolledCountByCourseId(100L)).thenReturn(0);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        CourseResponseDTO result = courseService.deleteCourse(100L);

        assertEquals("ARCHIVED", result.getStatus());
        verify(courseRepository).save(course);
    }

    @Test
    void submitChangeRequest_valid_createsPendingRequest() {
        CourseChangeRequestCreateDTO dto = CourseChangeRequestCreateDTO.builder()
                .courseId(100L)
                .requestedBy(5L)
                .requestType("CAPACITY")
                .proposedValue("50")
                .build();

        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(changeRequestRepository.save(any(CourseChangeRequest.class))).thenAnswer(i -> {
            CourseChangeRequest req = i.getArgument(0);
            req.setId(10L);
            return req;
        });

        CourseChangeRequestResponseDTO result = courseService.submitChangeRequest(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("PENDING", result.getStatus());
        assertEquals("30", result.getCurrentValue());
    }

    @Test
    void approveChangeRequest_pending_updatesCourseAndApproves() {
        CourseChangeRequest req = CourseChangeRequest.builder()
                .id(10L)
                .courseId(100L)
                .requestType("CAPACITY")
                .proposedValue("50")
                .status("PENDING")
                .build();

        when(changeRequestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(changeRequestRepository.save(any(CourseChangeRequest.class))).thenAnswer(i -> i.getArgument(0));

        CourseChangeRequestResponseDTO result = courseService.approveChangeRequest(10L, 1L, "Approved by dean");

        assertEquals("APPROVED", result.getStatus());
        assertEquals(50, course.getCapacity());
        verify(courseRepository).save(course);
    }
}
