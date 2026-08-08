package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CapacityCheckStrategyTest {

    private CapacityCheckStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CapacityCheckStrategy();
    }

    @Test
    void validate_seatAvailable_passes() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCapacity(30);
        section.setEnrolledCount(25);

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertDoesNotThrow(() -> strategy.validate(context));
    }

    @Test
    void validate_sectionFull_throwsValidationException() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCapacity(30);
        section.setEnrolledCount(30);

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertThrows(ValidationException.class, () -> strategy.validate(context));
    }

    @Test
    void validate_missingCapacityData_throwsValidationException() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCapacity(null);
        section.setEnrolledCount(0);

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertThrows(ValidationException.class, () -> strategy.validate(context));
    }
}
