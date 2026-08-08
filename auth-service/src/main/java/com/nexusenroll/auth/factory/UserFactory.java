package com.nexusenroll.auth.factory;

import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.common.util.StringUtil;

/**
 * Abstract Factory Method for creating User entities.
 */
public abstract class UserFactory {

    public abstract User createUser(RegisterRequestDTO request, String passwordHash) throws ValidationException;

    protected void populateCommonFields(User user, RegisterRequestDTO request, String passwordHash) throws ValidationException {
        if (request == null) {
            throw new ValidationException("Register request is required");
        }
        if (StringUtil.isEmpty(request.getUsername())) {
            throw new ValidationException("Username is required");
        }
        if (StringUtil.isEmpty(request.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (StringUtil.isEmpty(request.getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (StringUtil.isEmpty(request.getLastName())) {
            throw new ValidationException("Last name is required");
        }

        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordHash);
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(StringUtil.isEmpty(request.getPhoneNumber()) ? null : request.getPhoneNumber().trim());
        user.setStatus("ACTIVE");
        user.setLoginAttempts(0);
    }
}
