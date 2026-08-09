package com.nexusenroll.faculty.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.faculty.client.AcademicRecordClient;
import com.nexusenroll.faculty.dto.GradeDto;
import com.nexusenroll.faculty.dto.GradeOperationDto;
import com.nexusenroll.faculty.model.Grade;
import com.nexusenroll.faculty.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FacultyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AcademicRecordClient academicRecordClient;

    @BeforeEach
    void setUp() {
        gradeRepository.deleteAll();
    }

    @Test
    void shouldTransitionGradeFromDraftToApproved() throws Exception {
        // 1. Create Draft
        GradeDto draftRequest = GradeDto.builder()
                .enrollmentId(10L)
                .studentId(100L)
                .sectionId(200L)
                .assignmentTitle("Final Exam")
                .pointsEarned(85.0)
                .maxPoints(100.0)
                .letterGrade("B")
                .build();

        String draftResponseStr = mockMvc.perform(post("/faculty/grades/draft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(draftRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn().getResponse().getContentAsString();

        long gradeId = objectMapper.readTree(draftResponseStr).path("data").path("id").asLong();

        // 2. Submit Grade -> PENDING
        GradeOperationDto submitRequest = new GradeOperationDto(gradeId);

        mockMvc.perform(post("/faculty/grades/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        // 3. Approve Grade -> APPROVED
        GradeOperationDto approveRequest = new GradeOperationDto(gradeId);

        mockMvc.perform(post("/faculty/grades/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // Verify it was persisted correctly
        Grade savedGrade = gradeRepository.findById(gradeId).orElseThrow();
        assertEquals("APPROVED", savedGrade.getStatus());

        // Verify academic record client was called
        verify(academicRecordClient).recordGrade(any());
    }

    @Test
    void shouldRejectInvalidStateTransition() throws Exception {
        // 1. Create Draft
        GradeDto draftRequest = GradeDto.builder()
                .enrollmentId(11L)
                .studentId(101L)
                .sectionId(201L)
                .assignmentTitle("Midterm")
                .pointsEarned(95.0)
                .maxPoints(100.0)
                .letterGrade("A")
                .build();

        String draftResponseStr = mockMvc.perform(post("/faculty/grades/draft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(draftRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long gradeId = objectMapper.readTree(draftResponseStr).path("data").path("id").asLong();

        // 2. Try to Approve directly from Draft (Invalid transition)
        GradeOperationDto approveRequest = new GradeOperationDto(gradeId);

        mockMvc.perform(post("/faculty/grades/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approveRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid state transition")));
    }
}
