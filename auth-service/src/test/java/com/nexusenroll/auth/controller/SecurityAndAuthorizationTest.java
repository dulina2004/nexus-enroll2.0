package com.nexusenroll.auth.controller;

import com.nexusenroll.auth.model.AdminUser;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.security.JwtTokenProvider;
import com.nexusenroll.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityAndAuthorizationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void unauthenticatedUser_accessingProtectedEndpoint_returns401() throws Exception {
        mockMvc.perform(put("/api/auth/users/1/deactivate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401));
    }

    @Test
    void studentRole_accessingAdminEndpoint_returns403() throws Exception {
        User student = new StudentUser();
        student.setId(10L);
        student.setUsername("student1");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);

        String token = jwtTokenProvider.generateToken(student);

        mockMvc.perform(put("/api/auth/users/1/deactivate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403));
    }

    @Test
    void adminRole_accessingAdminEndpoint_returns200() throws Exception {
        User admin = new AdminUser();
        admin.setId(1L);
        admin.setUsername("admin1");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        when(authService.deactivateUser(anyLong(), anyLong())).thenReturn(admin);

        String token = jwtTokenProvider.generateToken(admin);

        mockMvc.perform(put("/api/auth/users/1/deactivate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}
