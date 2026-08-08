package com.nexusenroll.course;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot context load test for CourseServiceApplication using H2 test profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class CourseServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
