package com.nexusenroll.auth.factory;

import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.AdminUser;
import com.nexusenroll.auth.model.FacultyUser;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void testStudentFactory_createsStudentUser() throws Exception {
        UserFactory factory = new StudentFactory();
        RegisterRequestDTO req = createRequest("john_doe", "john@uni.edu", "STUDENT");
        User user = factory.createUser(req, "$2a$10$hashed_pass");

        assertNotNull(user);
        assertInstanceOf(StudentUser.class, user);
        assertEquals("john_doe", user.getUsername());
        assertEquals("$2a$10$hashed_pass", user.getPasswordHash());
    }

    @Test
    void testFacultyFactory_createsFacultyUser() throws Exception {
        UserFactory factory = new FacultyFactory();
        RegisterRequestDTO req = createRequest("prof_smith", "smith@uni.edu", "FACULTY");
        User user = factory.createUser(req, "$2a$10$hashed_pass");

        assertNotNull(user);
        assertInstanceOf(FacultyUser.class, user);
        assertEquals("prof_smith", user.getUsername());
    }

    @Test
    void testAdminFactory_createsAdminUser() throws Exception {
        UserFactory factory = new AdminFactory();
        RegisterRequestDTO req = createRequest("admin_user", "admin@uni.edu", "ADMIN");
        User user = factory.createUser(req, "$2a$10$hashed_pass");

        assertNotNull(user);
        assertInstanceOf(AdminUser.class, user);
        assertEquals("admin_user", user.getUsername());
    }

    private RegisterRequestDTO createRequest(String username, String email, String role) {
        return RegisterRequestDTO.builder()
                .username(username)
                .email(email)
                .password("password123")
                .firstName("First")
                .lastName("Last")
                .role(role)
                .build();
    }
}
