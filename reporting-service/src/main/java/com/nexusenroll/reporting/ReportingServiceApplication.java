package com.nexusenroll.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the NexusEnroll reporting-service.
 * Runs on port 8008.
 */
@SpringBootApplication(scanBasePackages = "com.nexusenroll")
public class ReportingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingServiceApplication.class, args);
    }
}
