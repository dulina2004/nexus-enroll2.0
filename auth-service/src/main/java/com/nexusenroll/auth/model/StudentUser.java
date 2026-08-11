package com.nexusenroll.auth.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Concrete Product in the Factory Method pattern. A {@link User} discriminated as STUDENT.
 */
@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
public class StudentUser extends User {
    public StudentUser() {
        super(Role.STUDENT);
    }
}
