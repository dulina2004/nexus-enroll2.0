package com.nexusenroll.academicrecord.controller;

import com.nexusenroll.academicrecord.dto.AcademicRecordDto;
import com.nexusenroll.academicrecord.dto.CompletedCourseDto;
import com.nexusenroll.academicrecord.dto.DegreeProgressDto;
import com.nexusenroll.academicrecord.dto.GradeRecordDto;
import com.nexusenroll.academicrecord.service.AcademicRecordService;
import com.nexusenroll.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AcademicRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AcademicRecordService academicRecordService;

    @InjectMocks
    private AcademicRecordController academicRecordController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(academicRecordController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /records/completed-courses returns 201 CREATED")
    void testAddCompletedCourse() throws Exception {
        CompletedCourseDto dto = CompletedCourseDto.builder()
                .id(1L)
                .studentId(101L)
                .courseCode("CS101")
                .credits(3)
                .grade("A")
                .build();

        when(academicRecordService.addCompletedCourse(eq(101L), any(CompletedCourseDto.class))).thenReturn(dto);

        String json = """
                {
                    "studentId": 101,
                    "courseCode": "CS101",
                    "credits": 3,
                    "grade": "A"
                }
                """;

        mockMvc.perform(post("/records/completed-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.courseCode").value("CS101"));
    }

    @Test
    @DisplayName("GET /records/completed-courses?studentId=101 returns 200 OK")
    void testGetCompletedCourses() throws Exception {
        CompletedCourseDto dto = CompletedCourseDto.builder()
                .id(1L)
                .studentId(101L)
                .courseCode("CS101")
                .build();

        when(academicRecordService.getCompletedCourses(101L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/records/completed-courses").param("studentId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].courseCode").value("CS101"));
    }

    @Test
    @DisplayName("GET /records/degree-progress?studentId=101 returns 200 OK")
    void testGetDegreeProgress() throws Exception {
        DegreeProgressDto progress = DegreeProgressDto.builder()
                .studentId(101L)
                .totalCreditsRequired(120)
                .totalCreditsCompleted(30)
                .progressPercentage(25.0)
                .build();

        when(academicRecordService.getDegreeProgress(101L)).thenReturn(progress);

        mockMvc.perform(get("/records/degree-progress").param("studentId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.progressPercentage").value(25.0));
    }

    @Test
    @DisplayName("GET /records?studentId=101 returns complete academic record")
    void testGetAcademicRecord() throws Exception {
        AcademicRecordDto record = AcademicRecordDto.builder()
                .studentId(101L)
                .cumulativeGpa(3.75)
                .totalCreditsEarned(45)
                .totalCreditsAttempted(45)
                .graduationStatus("IN_PROGRESS")
                .build();

        when(academicRecordService.getAcademicRecord(101L)).thenReturn(record);

        mockMvc.perform(get("/records").param("studentId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.cumulativeGpa").value(3.75));
    }
}
