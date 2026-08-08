package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.common.exception.ValidationException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete strategy validating that section capacity is not exceeded.
 */
@Component
@Order(2)
public class CapacityCheckStrategy implements EnrollmentValidationStrategy {

    @Override
    public void validate(EnrollmentValidationContext context) throws ServiceException {
        CourseSectionSnapshot section = context.getRequestedSection();
        if (section == null) {
            return;
        }
        if (section.getCapacity() == null || section.getEnrolledCount() == null) {
            throw new ValidationException("Course capacity data is unavailable");
        }
        if (section.getEnrolledCount() >= section.getCapacity()) {
            throw new ValidationException("Course section is full");
        }
    }
}
