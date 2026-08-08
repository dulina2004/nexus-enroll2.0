package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Concrete strategy validating that section schedule does not conflict with existing active enrollments.
 */
@Component
@Order(3)
public class TimeConflictCheckStrategy implements EnrollmentValidationStrategy {

    @Override
    public void validate(EnrollmentValidationContext context) throws ServiceException {
        CourseSectionSnapshot requested = context.getRequestedSection();
        if (requested == null || requested.getScheduleDays() == null || requested.getStartTime() == null || requested.getEndTime() == null) {
            return;
        }

        if (context.getCurrentSections() == null) {
            return;
        }

        for (CourseSectionSnapshot existing : context.getCurrentSections()) {
            if (existing.getSectionId() != null && existing.getSectionId().equals(requested.getSectionId())) {
                continue;
            }
            if (conflicts(requested, existing)) {
                throw new ValidationException("Course time conflicts with existing enrollment: " + existing.getCourseCode());
            }
        }
    }

    private boolean conflicts(CourseSectionSnapshot requested, CourseSectionSnapshot existing) {
        if (existing.getScheduleDays() == null || existing.getStartTime() == null || existing.getEndTime() == null) {
            return false;
        }

        Set<Character> requestedDays = normalizeDays(requested.getScheduleDays());
        Set<Character> existingDays = normalizeDays(existing.getScheduleDays());
        requestedDays.retainAll(existingDays);
        if (requestedDays.isEmpty()) {
            return false;
        }

        LocalTime requestedStart = requested.getStartTime().toLocalTime();
        LocalTime requestedEnd = requested.getEndTime().toLocalTime();
        LocalTime existingStart = existing.getStartTime().toLocalTime();
        LocalTime existingEnd = existing.getEndTime().toLocalTime();

        return requestedStart.isBefore(existingEnd) && existingStart.isBefore(requestedEnd);
    }

    private Set<Character> normalizeDays(String days) {
        Set<Character> normalized = new HashSet<>();
        for (char day : days.toUpperCase().toCharArray()) {
            if (!Character.isWhitespace(day) && day != ',') {
                normalized.add(day);
            }
        }
        return normalized;
    }
}
