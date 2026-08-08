package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeConflictCheckStrategyTest {

    private TimeConflictCheckStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TimeConflictCheckStrategy();
    }

    @Test
    void validate_differentDays_passes() {
        CourseSectionSnapshot current = new CourseSectionSnapshot();
        current.setSectionId(1L);
        current.setScheduleDays("MWF");
        current.setStartTime(Time.valueOf("09:00:00"));
        current.setEndTime(Time.valueOf("10:00:00"));

        CourseSectionSnapshot requested = new CourseSectionSnapshot();
        requested.setSectionId(2L);
        requested.setScheduleDays("TTH");
        requested.setStartTime(Time.valueOf("09:00:00"));
        requested.setEndTime(Time.valueOf("10:00:00"));

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                requested,
                List.of(current),
                Collections.emptyList()
        );

        assertDoesNotThrow(() -> strategy.validate(context));
    }

    @Test
    void validate_sameDayBackToBack_passes() {
        CourseSectionSnapshot current = new CourseSectionSnapshot();
        current.setSectionId(1L);
        current.setScheduleDays("MWF");
        current.setStartTime(Time.valueOf("09:00:00"));
        current.setEndTime(Time.valueOf("10:00:00"));

        CourseSectionSnapshot requested = new CourseSectionSnapshot();
        requested.setSectionId(2L);
        requested.setScheduleDays("MWF");
        requested.setStartTime(Time.valueOf("10:00:00"));
        requested.setEndTime(Time.valueOf("11:00:00"));

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                requested,
                List.of(current),
                Collections.emptyList()
        );

        assertDoesNotThrow(() -> strategy.validate(context));
    }

    @Test
    void validate_overlappingTime_throwsValidationException() {
        CourseSectionSnapshot current = new CourseSectionSnapshot();
        current.setSectionId(1L);
        current.setCourseCode("CS101");
        current.setScheduleDays("MWF");
        current.setStartTime(Time.valueOf("09:00:00"));
        current.setEndTime(Time.valueOf("10:30:00"));

        CourseSectionSnapshot requested = new CourseSectionSnapshot();
        requested.setSectionId(2L);
        requested.setScheduleDays("MWF");
        requested.setStartTime(Time.valueOf("10:00:00"));
        requested.setEndTime(Time.valueOf("11:00:00"));

        EnrollmentValidationContext context = new EnrollmentValidationContext(
                new Enrollment(),
                requested,
                List.of(current),
                Collections.emptyList()
        );

        assertThrows(ValidationException.class, () -> strategy.validate(context));
    }
}
