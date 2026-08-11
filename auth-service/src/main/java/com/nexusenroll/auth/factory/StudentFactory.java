package com.nexusenroll.auth.factory;

import com.nexusenroll.auth.dto.RegisterRequestDTO;
import com.nexusenroll.auth.model.Role;
import com.nexusenroll.auth.model.StudentUser;
import com.nexusenroll.auth.model.User;
import com.nexusenroll.common.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator in the Factory Method pattern. Builds a {@link StudentUser}
 * with the STUDENT role and the fields common to every user type.
 */
@Component
public class StudentFactory extends UserFactory {

    @Override
    public User createUser(RegisterRequestDTO request, String passwordHash) throws ValidationException {
        StudentUser user = new StudentUser();
        populateCommonFields(user, request, passwordHash);
        user.setRole(Role.STUDENT);
        return user;
    }
}
