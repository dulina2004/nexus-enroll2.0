package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrerequisiteCheckStrategyTest {

    private PrerequisiteCheckStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PrerequisiteCheckStrategy();
    }

    @Test
    void validate_noPrerequisites_passes() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCourseCode("CS101");
        section.setPrerequisites(null);

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertDoesNotThrow(() -> strategy.validate(context));
    }

    @Test
    void validate_prerequisiteMet_passes() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCourseCode("CS201");
        section.setPrerequisites("CS101");

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                List.of("CS101")
        );

        assertDoesNotThrow(() -> strategy.validate(context));
    }

    @Test
    void validate_prerequisiteMissing_throwsValidationException() {
        CourseSectionSnapshot section = new CourseSectionSnapshot();
        section.setCourseCode("CS201");
        section.setPrerequisites("CS101");

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                section,
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertThrows(ValidationException.class, () -> strategy.validate(context));
    }
}
