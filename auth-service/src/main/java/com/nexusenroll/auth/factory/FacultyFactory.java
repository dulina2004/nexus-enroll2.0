package com.nexusenroll.auth.factory;

import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.FacultyUser;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.common.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator in the Factory Method pattern. Builds a {@link FacultyUser}
 * with the FACULTY role and the fields common to every user type.
 */
@Component
public class FacultyFactory extends UserFactory {

    @Override
    public User createUser(RegisterRequestDTO request, String passwordHash) throws ValidationException {
        FacultyUser user = new FacultyUser();
        populateCommonFields(user, request, passwordHash);
        user.setRole(Role.FACULTY);
        return user;
    }
}
