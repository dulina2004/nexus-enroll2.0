package com.nexusenroll.auth.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Concrete Product in the Factory Method pattern. A {@link User} discriminated as ADMIN.
 */
@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
public class AdminUser extends User {
    public AdminUser() {
        super(Role.ADMIN);
    }
}
