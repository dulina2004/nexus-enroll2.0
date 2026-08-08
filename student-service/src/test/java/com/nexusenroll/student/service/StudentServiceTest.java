package com.nexusenroll.student.service;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.student.dto.ScheduleDTO;
import com.nexusenroll.student.dto.StudentRequestDTO;
import com.nexusenroll.student.dto.StudentResponseDTO;
import com.nexusenroll.student.model.Student;
import com.nexusenroll.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .userId(10L)
                .studentId("STU001")
                .enrollmentDate(LocalDate.of(2023, 9, 1))
                .status("ACTIVE")
                .gpa(new BigDecimal("3.75"))
                .totalCreditsEarned(30)
                .city("New York")
                .build();
    }

    @Test
    void getStudent_existingId_returnsDTO() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponseDTO dto = studentService.getStudent(1L);

        assertNotNull(dto);
        assertEquals("STU001", dto.getStudentId());
        assertEquals(new BigDecimal("3.75"), dto.getGpa());
    }

    @Test
    void getStudent_nonExistingId_throwsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudent(99L));
    }

    @Test
    void getStudentByUserId_existing_returnsDTO() {
        when(studentRepository.findByUserId(10L)).thenReturn(Optional.of(student));

        StudentResponseDTO dto = studentService.getStudentByUserId(10L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void updateStudent_valid_updatesFields() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        StudentRequestDTO req = StudentRequestDTO.builder()
                .city("San Francisco")
                .status("ACTIVE")
                .build();

        StudentResponseDTO dto = studentService.updateStudent(1L, req);

        assertEquals("San Francisco", dto.getCity());
        verify(studentRepository).save(student);
    }

    @Test
    void getSchedule_emptyEnrollments_returnsEmptySchedule() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        ScheduleDTO schedule = studentService.getSchedule(1L, "Fall", 2024);

        assertNotNull(schedule);
        assertEquals(1L, schedule.getStudentId());
        assertTrue(schedule.getItems().isEmpty());
    }
}
