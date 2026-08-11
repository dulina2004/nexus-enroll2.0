package com.nexusenroll.auth.service;

import com.nexusenroll.auth.dto.AuthResponseDTO;
import com.nexusenroll.auth.dto.LoginRequestDTO;
import com.nexusenroll.auth.dto.ProvisionStaffRequestDTO;
import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.factory.AdminFactory;
import com.nexusenroll.auth.factory.FacultyFactory;
import com.nexusenroll.auth.factory.StudentFactory;
import com.nexusenroll.auth.factory.UserFactory;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.repository.UserRepository;
import com.nexusenroll.auth.security.JwtTokenProvider;
import com.nexusenroll.common.exception.AuthenticationException;
import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Holds the {@code Role -> UserFactory} map that drives the Factory Method pattern: registration
 * and staff provisioning look up the matching {@link UserFactory} by role instead of branching on
 * type. Adding a role means adding a factory and one map entry here; no existing factory or
 * calling code needs to change (Open/Closed Principle).
 */
@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<Role, UserFactory> factories;
    private final long jwtExpirationMs;

    public AuthService(UserRepository userRepository,
                       SessionService sessionService,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       StudentFactory studentFactory,
                       FacultyFactory facultyFactory,
                       AdminFactory adminFactory,
                       @Value("${jwt.expiration-ms:28800000}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
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

        // Public self-registration only ever creates STUDENT accounts.
        // FACULTY/ADMIN accounts can only be created by an admin via
        // provisionStaffAccount() below - otherwise anyone could self-register
        // as an administrator by putting "role":"ADMIN" in the request body.
        Role role;
        try {
            role = Role.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unsupported role: " + request.getRole());
        }
        if (role != Role.STUDENT) {
            throw new ValidationException("Public registration is only available for students. Contact an administrator for a faculty or admin account.");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ValidationException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = factories.get(Role.STUDENT).createUser(request, hashedPassword);
        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser);
        long expiresAt = System.currentTimeMillis() + jwtExpirationMs;

        trySaveSession(savedUser.getId(), hashToken(token), Instant.ofEpochMilli(expiresAt));

        return AuthResponseDTO.fromUser(savedUser, token, expiresAt);
    }

    /**
     * Admin-only: provisions a FACULTY or ADMIN account. Unlike register(),
     * this does not log the new user in - it just creates the account for
     * them to log into separately.
     */
    public User provisionStaffAccount(ProvisionStaffRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        Role role;
        try {
            role = Role.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unsupported role: " + request.getRole());
        }
        if (role != Role.FACULTY && role != Role.ADMIN) {
            throw new ValidationException("provision-staff can only create FACULTY or ADMIN accounts");
        }

        RegisterRequestDTO asRegisterRequest = RegisterRequestDTO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();
        validateRegisterRequest(asRegisterRequest);

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ValidationException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = factories.get(role).createUser(asRegisterRequest, hashedPassword);
        return userRepository.save(user);
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

        trySaveSession(user.getId(), hashToken(token), Instant.ofEpochMilli(expiresAt));

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

    /**
     * SessionService.saveSession runs in its own REQUIRES_NEW transaction and
     * already catches write failures internally - but a REQUIRES_NEW
     * transaction that Hibernate marked rollback-only (e.g. after a lock-wait
     * timeout during flush) still throws UnexpectedRollbackException from the
     * transactional proxy's own commit step, which happens *after* that
     * method body returns and so is outside its internal try/catch. Without
     * this wrapper, that exception propagates into register()/login() and
     * fails the whole request (and rolls back the user row) over what should
     * be a fire-and-forget audit write.
     */
    private void trySaveSession(Long userId, String tokenHash, Instant expiresAt) {
        try {
            sessionService.saveSession(userId, tokenHash, expiresAt);
        } catch (Exception e) {
            log.warn("Session persistence failed for user {}: {}", userId, e.getMessage());
        }
    }

    private String hashToken(String token) {
        if (token == null) return "";
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return token.length() > 255 ? token.substring(0, 255) : token;
        }
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
