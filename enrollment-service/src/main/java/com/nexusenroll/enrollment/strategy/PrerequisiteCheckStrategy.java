package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Concrete strategy validating that student has completed all course prerequisites.
 */
@Component
@Order(1)
public class PrerequisiteCheckStrategy implements EnrollmentValidationStrategy {

    @Override
    public void validate(EnrollmentValidationContext context) throws ServiceException {
        CourseSectionSnapshot section = context.getRequestedSection();
        if (section == null) {
            return;
        }
        String prerequisites = section.getPrerequisites();
        if (prerequisites == null || prerequisites.isBlank()) {
            return;
        }

        Set<String> completed = new HashSet<>(context.getCompletedCourseCodes() != null ? context.getCompletedCourseCodes() : java.util.Collections.emptyList());
        for (String required : Arrays.stream(prerequisites.split(",")).map(String::trim).filter(value -> !value.isEmpty()).toList()) {
            if (!completed.contains(required)) {
                throw new ValidationException("Missing prerequisite course: " + required);
            }
        }
    }
}
