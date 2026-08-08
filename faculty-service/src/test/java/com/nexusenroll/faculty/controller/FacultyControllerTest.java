package com.nexusenroll.faculty.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.faculty.dto.FacultyDto;
import com.nexusenroll.faculty.dto.RosterDto;
import com.nexusenroll.faculty.service.FacultyService;
import com.nexusenroll.faculty.service.GradeService;
import com.nexusenroll.faculty.state.GradeContext;
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

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyService;

    @Mock
    private GradeService gradeService;

    @InjectMocks
    private FacultyController facultyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facultyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /faculty/roster?sectionId=1 returns 200 OK")
    void testGetRoster() throws Exception {
        RosterDto roster = RosterDto.builder()
                .sectionId(1L)
                .courseCode("CS101")
                .courseTitle("Intro to Computer Science")
                .semester("SPRING")
                .year(2026)
                .students(new ArrayList<>())
                .build();

        when(facultyService.getClassRoster(1L)).thenReturn(roster);

        mockMvc.perform(get("/faculty/roster").param("sectionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.courseCode").value("CS101"));
    }

    @Test
    @DisplayName("GET /faculty/1 returns 200 OK")
    void testGetFacultyById() throws Exception {
        FacultyDto dto = FacultyDto.builder()
                .id(1L)
                .facultyId("FAC-100")
                .title("Associate Professor")
                .build();

        when(facultyService.getFaculty(1L)).thenReturn(dto);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.facultyId").value("FAC-100"));
    }

    @Test
    @DisplayName("POST /faculty/grades/submit updates grade status to PENDING")
    void testSubmitGrade() throws Exception {
        GradeContext submitted = new GradeContext(1L, 101L, 201L, 1L, "Quiz 1", 90.0, 100.0, "A", null, "FACULTY", "PENDING");

        when(gradeService.submitGrade(1L)).thenReturn(submitted);

        mockMvc.perform(post("/faculty/grades/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gradeId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
}
