package com.nexusenroll.auth.service;

import com.nexusenroll.auth.dto.AuthResponseDTO;
import com.nexusenroll.auth.dto.LoginRequestDTO;
import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.factory.AdminFactory;
import com.nexusenroll.auth.factory.FacultyFactory;
import com.nexusenroll.auth.factory.StudentFactory;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.repository.SessionRepository;
import com.nexusenroll.auth.repository.UserRepository;
import com.nexusenroll.auth.security.JwtTokenProvider;
import com.nexusenroll.common.exception.AuthenticationException;
import com.nexusenroll.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;
    private User testUser;

    @BeforeEach
    void setUp() {
        StudentFactory studentFactory = new StudentFactory();
        FacultyFactory facultyFactory = new FacultyFactory();
        AdminFactory adminFactory = new AdminFactory();

        authService = new AuthService(
                userRepository,
                sessionRepository,
                passwordEncoder,
                jwtTokenProvider,
                studentFactory,
                facultyFactory,
                adminFactory,
                3600000L
        );

        testUser = new StudentUser();
        testUser.setId(1L);
        testUser.setUsername("alice");
        testUser.setEmail("alice@test.com");
        testUser.setPasswordHash("$2a$10$encodedPasswordHash");
        testUser.setStatus("ACTIVE");
        testUser.setLoginAttempts(0);
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void login_validCredentials_returnsTokenAndUser() {
        LoginRequestDTO req = new LoginRequestDTO("alice", "Password123!");

        when(userRepository.findByIdentifier("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "$2a$10$encodedPasswordHash")).thenReturn(true);
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("jwt.token.string");

        AuthResponseDTO response = authService.login(req);

        assertNotNull(response);
        assertEquals("alice", response.getUsername());
        assertEquals("jwt.token.string", response.getToken());
        verify(userRepository).save(testUser);
    }

    @Test
    void login_invalidPassword_throwsAuthenticationException() {
        LoginRequestDTO req = new LoginRequestDTO("alice", "WrongPassword");

        when(userRepository.findByIdentifier("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$encodedPasswordHash")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.login(req));
        verify(userRepository).save(testUser);
    }

    @Test
    void login_userNotFound_throwsAuthenticationException() {
        LoginRequestDTO req = new LoginRequestDTO("unknown", "Password123!");

        when(userRepository.findByIdentifier("unknown")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.login(req));
    }

    @Test
    void register_hashesPassword_andCreatesUser() {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .username("bob")
                .email("bob@test.com")
                .password("Secret123!")
                .firstName("Bob")
                .lastName("Builder")
                .role("STUDENT")
                .build();

        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("$2a$10$hashedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("generated.jwt.token");

        AuthResponseDTO response = authService.register(req);

        assertNotNull(response);
        assertEquals("bob", response.getUsername());
        assertEquals("generated.jwt.token", response.getToken());
        verify(passwordEncoder).encode("Secret123!");
    }

    @Test
    void register_existingUsername_throwsValidationException() {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .username("alice")
                .email("alice2@test.com")
                .password("Secret123!")
                .firstName("Alice")
                .lastName("Smith")
                .role("STUDENT")
                .build();

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(ValidationException.class, () -> authService.register(req));
    }
}
