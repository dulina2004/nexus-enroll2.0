package com.nexusenroll.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Admin-only request to create a FACULTY or ADMIN account. Separate from
 * RegisterRequestDTO because public self-registration (/api/auth/register)
 * is restricted to STUDENT accounts - staff accounts can only be created
 * through this admin-gated endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvisionStaffRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phoneNumber;

    @NotBlank(message = "Role is required")
    private String role;

    /** Informational only - the User entity has no department field. */
    private String department;
}
