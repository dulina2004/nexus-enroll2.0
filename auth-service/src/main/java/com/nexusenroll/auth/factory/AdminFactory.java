package com.nexusenroll.auth.factory;

import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.AdminUser;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.common.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AdminFactory extends UserFactory {

    @Override
    public User createUser(RegisterRequestDTO request, String passwordHash) throws ValidationException {
        AdminUser user = new AdminUser();
        populateCommonFields(user, request, passwordHash);
        user.setRole(Role.ADMIN);
        return user;
    }
}
