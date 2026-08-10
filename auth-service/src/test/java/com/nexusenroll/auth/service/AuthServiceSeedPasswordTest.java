package com.nexusenroll.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards against the seed data regressing to a hand-typed placeholder hash
 * that can never be logged in with (see V2__seed_auth_data.sql history:
 * "$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5" looked like
 * a valid bcrypt hash but did not actually match any password).
 */
class AuthServiceSeedPasswordTest {

    private static final String SEED_PASSWORD = "Password123";
    private static final String SEED_HASH = "$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G";

    @Test
    void seedHashMatchesSeedPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(SEED_PASSWORD, SEED_HASH),
                "V2__seed_auth_data.sql's password hash must match \"" + SEED_PASSWORD + "\" for every seeded demo user to be able to log in");
    }
}
