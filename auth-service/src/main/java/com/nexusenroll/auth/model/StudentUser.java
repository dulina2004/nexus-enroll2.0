package com.nexusenroll.auth.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
public class StudentUser extends User {
    public StudentUser() {
        super(Role.STUDENT);
    }
}
