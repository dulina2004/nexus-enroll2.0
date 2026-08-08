package com.nexusenroll.auth.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
public class AdminUser extends User {
    public AdminUser() {
        super(Role.ADMIN);
    }
}
