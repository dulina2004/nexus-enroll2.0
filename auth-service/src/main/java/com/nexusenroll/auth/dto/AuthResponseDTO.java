package com.nexusenroll.auth.dto;

import com.nexusenroll.auth.model.User;
import lombok.*;

/**
 * Response returned after successful login or registration: user profile fields plus the issued JWT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String token;
    private Long tokenExpiresAt;

    public static AuthResponseDTO fromUser(User user) {
        return fromUser(user, null, null);
    }

    public static AuthResponseDTO fromUser(User user, String token, Long tokenExpiresAt) {
        if (user == null) return null;
        return AuthResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .token(token)
                .tokenExpiresAt(tokenExpiresAt)
                .build();
    }
}
