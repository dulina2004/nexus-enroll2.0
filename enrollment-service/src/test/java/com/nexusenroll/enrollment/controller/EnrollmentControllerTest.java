package com.nexusenroll.enrollment.controller;

import com.nexusenroll.common.exception.GlobalExceptionHandler;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import com.nexusenroll.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private Enrollment sampleEnrollment;
    private WaitlistEntry sampleWaitlist;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleEnrollment = new Enrollment(1L, 100L, LocalDate.now(), "ENROLLED");
        sampleEnrollment.setId(10L);

        sampleWaitlist = new WaitlistEntry(1L, 100L, 1, "WAITING");
        sampleWaitlist.setId(5L);
    }

    @Test
    void createEnrollment_success() throws Exception {
        when(enrollmentService.enroll(1L, 100L)).thenReturn(sampleEnrollment);

        String json = """
                {
                    "studentId": 1,
                    "sectionId": 100
                }
                """;

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.status").value("ENROLLED"));
    }

    @Test
    void createEnrollment_validationFailure_returns400() throws Exception {
        when(enrollmentService.enroll(1L, 100L))
                .thenThrow(new ValidationException("Course section is full"));

        String json = """
                {
                    "studentId": 1,
                    "sectionId": 100
                }
                """;

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Course section is full"));
    }

    @Test
    void overrideEnrollment_success() throws Exception {
        when(enrollmentService.enrollWithOverride(1L, 100L, 99L, "Capacity override"))
                .thenReturn(sampleEnrollment);

        String json = """
                {
                    "studentId": 1,
                    "sectionId": 100,
                    "adminUserId": 99,
                    "reason": "Capacity override"
                }
                """;

        mockMvc.perform(post("/enrollments/override")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void dropEnrollment_success() throws Exception {
        sampleEnrollment.setStatus("DROPPED");
        when(enrollmentService.drop(10L)).thenReturn(sampleEnrollment);

        mockMvc.perform(delete("/enrollments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.status").value("DROPPED"));
    }

    @Test
    void getEnrollmentsForStudent_success() throws Exception {
        when(enrollmentService.getEnrollmentsForStudent(1L)).thenReturn(List.of(sampleEnrollment));

        mockMvc.perform(get("/enrollments?studentId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(10));
    }

    @Test
    void joinWaitlist_success() throws Exception {
        when(enrollmentService.joinWaitlist(1L, 100L)).thenReturn(sampleWaitlist);

        String json = """
                {
                    "studentId": 1,
                    "sectionId": 100
                }
                """;

        mockMvc.perform(post("/enrollments/waitlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.position").value(1));
    }

    @Test
    void cancelWaitlist_success() throws Exception {
        mockMvc.perform(delete("/enrollments/waitlist/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
