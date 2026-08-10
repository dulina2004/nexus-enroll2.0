package com.nexusenroll.auth.controller;

import com.nexusenroll.auth.dto.AuthResponseDTO;
import com.nexusenroll.auth.dto.LoginRequestDTO;
import com.nexusenroll.auth.dto.ProvisionStaffRequestDTO;
import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.service.AuthService;
import com.nexusenroll.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for login, user registration, and account management.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("User registered successfully", response));
    }

    @PostMapping("/provision-staff")
    public ResponseEntity<ApiResponse<User>> provisionStaffAccount(@Valid @RequestBody ProvisionStaffRequestDTO request) {
        User user = authService.provisionStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Staff account created successfully", user));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getRoles() {
        List<Role> roles = authService.getAvailableRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", roles));
    }

    @PutMapping("/users/{id:\\d+}/deactivate")
    public ResponseEntity<ApiResponse<User>> deactivateUser(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Long> body) {
        Long adminUserId = (body != null && body.containsKey("adminUserId")) ? body.get("adminUserId") : 0L;
        User user = authService.deactivateUser(id, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("User account deactivated successfully", user));
    }

    @PutMapping("/users/{id:\\d+}/reactivate")
    public ResponseEntity<ApiResponse<User>> reactivateUser(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Long> body) {
        Long adminUserId = (body != null && body.containsKey("adminUserId")) ? body.get("adminUserId") : 0L;
        User user = authService.reactivateUser(id, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("User account reactivated successfully", user));
    }
}
