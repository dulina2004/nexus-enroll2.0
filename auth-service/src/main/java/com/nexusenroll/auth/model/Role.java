package com.nexusenroll.auth.model;

import com.nexusenroll.common.util.StringUtil;

/**
 * The three account roles NexusEnroll recognises, and the key the Factory Method
 * and Role -> UserFactory map in {@link com.nexusenroll.auth.service.AuthService} dispatch on.
 */
public enum Role {
    STUDENT,
    FACULTY,
    ADMIN;

    public static Role fromString(String value) {
        if (StringUtil.isEmpty(value)) {
            return STUDENT;
        }
        return Role.valueOf(value.trim().toUpperCase());
    }
}
