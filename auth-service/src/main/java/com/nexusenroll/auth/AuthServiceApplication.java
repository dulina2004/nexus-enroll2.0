package com.nexusenroll.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the NexusEnroll auth-service.
 * Runs on port 8001.
 */
@SpringBootApplication(scanBasePackages = "com.nexusenroll")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
