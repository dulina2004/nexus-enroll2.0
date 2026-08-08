package com.nexusenroll.auth.service;

import com.nexusenroll.auth.dto.AuthResponseDTO;
import com.nexusenroll.auth.dto.LoginRequestDTO;
import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.factory.AdminFactory;
import com.nexusenroll.auth.factory.FacultyFactory;
import com.nexusenroll.auth.factory.StudentFactory;
import com.nexusenroll.auth.factory.UserFactory;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.Session;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.repository.SessionRepository;
import com.nexusenroll.auth.repository.UserRepository;
import com.nexusenroll.auth.security.JwtTokenProvider;
import com.nexusenroll.common.exception.AuthenticationException;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Service for authentication, user registration, JWT session management, and account status updates.
 */
@Service
@Transactional
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<Role, UserFactory> factories;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       StudentFactory studentFactory,
                       FacultyFactory facultyFactory,
                       AdminFactory adminFactory,
                       @Value("${jwt.expiration-ms:28800000}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpirationMs = jwtExpirationMs;

        this.factories = new EnumMap<>(Role.class);
        this.factories.put(Role.STUDENT, studentFactory);
        this.factories.put(Role.FACULTY, facultyFactory);
        this.factories.put(Role.ADMIN, adminFactory);
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        validateRegisterRequest(request);

        Role role;
        try {
            role = Role.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unsupported role: " + request.getRole());
        }

        UserFactory factory = factories.get(role);
        if (factory == null) {
            throw new ValidationException("Unsupported role: " + request.getRole());
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ValidationException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = factory.createUser(request, hashedPassword);
        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser);
        long expiresAt = System.currentTimeMillis() + jwtExpirationMs;

        saveSession(savedUser.getId(), token, expiresAt);

        return AuthResponseDTO.fromUser(savedUser, token, expiresAt);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        validateLoginRequest(request);

        User user = userRepository.findByIdentifier(request.getIdentifier().trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username/email or password"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new AuthenticationException("User account is inactive (Status: " + user.getStatus() + ")");
        }

        Instant now = Instant.now();
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(now)) {
            throw new AuthenticationException("Account is locked until " + user.getLockedUntil());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int failedAttempts = (user.getLoginAttempts() == null ? 0 : user.getLoginAttempts()) + 1;
            user.setLoginAttempts(failedAttempts);
            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                user.setLockedUntil(now.plus(LOCK_MINUTES, ChronoUnit.MINUTES));
            }
            userRepository.save(user);
            throw new AuthenticationException("Invalid username/email or password");
        }

        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(now);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        long expiresAt = System.currentTimeMillis() + jwtExpirationMs;

        saveSession(user.getId(), token, expiresAt);

        return AuthResponseDTO.fromUser(user, token, expiresAt);
    }

    @Transactional(readOnly = true)
    public List<Role> getAvailableRoles() {
        return List.of(Role.STUDENT, Role.FACULTY, Role.ADMIN);
    }

    public User deactivateUser(Long userId, Long adminUserId) {
        return setUserStatus(userId, "INACTIVE");
    }

    public User reactivateUser(Long userId, Long adminUserId) {
        return setUserStatus(userId, "ACTIVE");
    }

    private User setUserStatus(Long userId, String status) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID is required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setStatus(status);
        return userRepository.save(user);
    }

    private void saveSession(Long userId, String token, long expiresAtMs) {
        try {
            Session session = Session.builder()
                    .userId(userId)
                    .tokenHash(token)
                    .expiresAt(Instant.ofEpochMilli(expiresAtMs))
                    .lastActivityAt(Instant.now())
                    .build();
            sessionRepository.save(session);
        } catch (Exception ignored) {}
    }

    private void validateRegisterRequest(RegisterRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        if (StringUtil.isEmpty(request.getUsername())) {
            throw new ValidationException("Username is required");
        }
        if (!StringUtil.isValidEmail(request.getEmail())) {
            throw new ValidationException("Valid email is required");
        }
        if (StringUtil.isEmpty(request.getPassword()) || request.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (StringUtil.isEmpty(request.getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (StringUtil.isEmpty(request.getLastName())) {
            throw new ValidationException("Last name is required");
        }
    }

    private void validateLoginRequest(LoginRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        if (StringUtil.isEmpty(request.getIdentifier())) {
            throw new ValidationException("Username or email is required");
        }
        if (StringUtil.isEmpty(request.getPassword())) {
            throw new ValidationException("Password is required");
        }
    }
}
