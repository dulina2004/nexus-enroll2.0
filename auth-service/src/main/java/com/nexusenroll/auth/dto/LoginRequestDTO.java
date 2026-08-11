package com.nexusenroll.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Login request: a username-or-email identifier plus password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
