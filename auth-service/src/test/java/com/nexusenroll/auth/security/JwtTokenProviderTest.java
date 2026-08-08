package com.nexusenroll.auth.security;

import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "c3VwZXJzZWNyZXRqd3RzaWduaW5na2V5bmV4dXNlbnJvbGxtaWdyYXRpb24yMDI2";
        jwtTokenProvider = new JwtTokenProvider(secret, 3600000);
    }

    @Test
    void generateToken_and_validateToken_success() {
        User user = new StudentUser();
        user.setId(100L);
        user.setUsername("teststudent");
        user.setEmail("student@test.com");
        user.setRole(Role.STUDENT);

        String token = jwtTokenProvider.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("teststudent", jwtTokenProvider.getUsernameFromToken(token));
        assertEquals("STUDENT", jwtTokenProvider.getRoleFromToken(token));
        assertEquals(100L, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.jwt.token"));
    }
}
