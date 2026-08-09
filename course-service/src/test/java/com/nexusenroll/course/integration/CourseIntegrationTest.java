package com.nexusenroll.course.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusenroll.course.dto.CourseRequestDTO;
import com.nexusenroll.course.dto.CourseUpdateCapacityDTO;
import com.nexusenroll.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private com.nexusenroll.course.repository.DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long defaultDepartmentId;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        departmentRepository.deleteAll();

        com.nexusenroll.course.model.Department dept = new com.nexusenroll.course.model.Department();
        dept.setCode("COMP");
        dept.setName("Computer Science");
        dept = departmentRepository.save(dept);
        defaultDepartmentId = dept.getId();
    }

    @Test
    void shouldCreateCourseSuccessfully() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setCourseCode("TEST101");
        request.setCourseNumber(101);
        request.setTitle("Integration Testing");
        request.setDescription("A test course");
        request.setCredits(3);
        request.setDepartmentId(defaultDepartmentId);
        request.setLevel("UNDERGRADUATE");
        request.setStatus("ACTIVE");

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.courseCode").value("TEST101"))
                .andExpect(jsonPath("$.data.title").value("Integration Testing"));
    }

    @Test
    void shouldSearchCourses() throws Exception {
        CourseRequestDTO createRequest = new CourseRequestDTO();
        createRequest.setCourseCode("SRCH101");
        createRequest.setCourseNumber(101);
        createRequest.setTitle("Search Test");
        createRequest.setCredits(3);
        createRequest.setDepartmentId(defaultDepartmentId);
        createRequest.setLevel("UNDERGRADUATE");
        createRequest.setStatus("ACTIVE");

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/courses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThan(0)));
    }

    @Test
    void shouldUpdateCapacity() throws Exception {
        CourseRequestDTO createRequest = new CourseRequestDTO();
        createRequest.setCourseCode("CAP101");
        createRequest.setCourseNumber(101);
        createRequest.setTitle("Capacity Test");
        createRequest.setDescription("A course to test capacity");
        createRequest.setCredits(3);
        createRequest.setDepartmentId(defaultDepartmentId);
        createRequest.setLevel("UNDERGRADUATE");
        createRequest.setStatus("ACTIVE");

        String responseStr = mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long courseId = objectMapper.readTree(responseStr).path("data").path("id").asLong();

        CourseUpdateCapacityDTO request = new CourseUpdateCapacityDTO();
        request.setCapacity(50);

        mockMvc.perform(put("/api/courses/" + courseId + "/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.capacity").value(50));
    }
}
