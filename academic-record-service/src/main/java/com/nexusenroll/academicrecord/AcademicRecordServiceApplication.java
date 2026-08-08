package com.nexusenroll.academicrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the NexusEnroll academic-record-service.
 * Runs on port 8006.
 */
@SpringBootApplication
public class AcademicRecordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicRecordServiceApplication.class, args);
    }
}
