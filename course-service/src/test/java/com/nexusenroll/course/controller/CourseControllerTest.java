package com.nexusenroll.course.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.course.dto.CourseResponseDTO;
import com.nexusenroll.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCourses_returnsSuccessResponse() throws Exception {
        CourseResponseDTO c = CourseResponseDTO.builder()
                .id(1L)
                .courseCode("CS101")
                .title("Intro to CS")
                .credits(3)
                .build();

        when(courseService.getCourses(any())).thenReturn(List.of(c));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Courses retrieved successfully"))
                .andExpect(jsonPath("$.data[0].courseCode").value("CS101"));
    }

    @Test
    void getCourse_notFound_returns404() throws Exception {
        when(courseService.getCourse(999L)).thenThrow(new ResourceNotFoundException("Course not found: 999"));

        mockMvc.perform(get("/api/courses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: 999"));
    }

    @Test
    void createCourse_valid_returns201() throws Exception {
        CourseResponseDTO c = CourseResponseDTO.builder()
                .id(2L)
                .courseCode("CS102")
                .title("Data Structures")
                .build();

        when(courseService.createCourse(any())).thenReturn(c);

        String json = """
                {
                    "courseCode": "CS102",
                    "courseNumber": 102,
                    "title": "Data Structures",
                    "credits": 4,
                    "departmentId": 1
                }
                """;

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Course created successfully"))
                .andExpect(jsonPath("$.data.courseCode").value("CS102"));
    }
}
