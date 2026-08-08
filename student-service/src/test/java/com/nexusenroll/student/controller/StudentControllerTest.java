package com.nexusenroll.student.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.student.dto.StudentResponseDTO;
import com.nexusenroll.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getStudentById_returns200() throws Exception {
        StudentResponseDTO s = StudentResponseDTO.builder()
                .id(1L)
                .studentId("STU001")
                .gpa(new BigDecimal("3.80"))
                .build();

        when(studentService.getStudent(1L)).thenReturn(s);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.studentId").value("STU001"));
    }

    @Test
    void getStudentById_notFound_returns404() throws Exception {
        when(studentService.getStudent(99L)).thenThrow(new ResourceNotFoundException("Student not found: 99"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }
}
