package com.nexusenroll.auth.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FACULTY")
@Getter
@Setter
public class FacultyUser extends User {
    public FacultyUser() {
        super(Role.FACULTY);
    }
}
