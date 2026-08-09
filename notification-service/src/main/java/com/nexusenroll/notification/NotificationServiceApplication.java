package com.nexusenroll.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the NexusEnroll notification-service.
 * Runs on port 8007.
 */
@SpringBootApplication(scanBasePackages = "com.nexusenroll")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
