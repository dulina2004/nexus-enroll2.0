package com.nexusenroll.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the NexusEnroll course-service.
 * Runs on port 8003.
 */
@SpringBootApplication(scanBasePackages = "com.nexusenroll")
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}
