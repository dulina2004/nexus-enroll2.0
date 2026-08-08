package com.nexusenroll.auth.model;

import com.nexusenroll.common.util.StringUtil;

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
