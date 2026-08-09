package com.nexusenroll.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusenroll.auth.dto.LoginRequestDTO;
import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        StudentUser testUser = new StudentUser();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@test.com");
        testUser.setPasswordHash(passwordEncoder.encode("Password123!"));
        testUser.setRole(Role.STUDENT);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setStatus("ACTIVE");
        userRepository.save(testUser);
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("testuser", "Password123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("testuser", "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectLoginWithUnknownUser() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("unknown", "Password123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterNewStudentSuccessfully() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newstudent")
                .email("newstudent@test.com")
                .password("Password123!")
                .firstName("New")
                .lastName("Student")
                .role("STUDENT")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("newstudent"));
    }

    @Test
    void shouldRejectRegistrationWithDuplicateUsername() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .email("another@test.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .role("STUDENT")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
