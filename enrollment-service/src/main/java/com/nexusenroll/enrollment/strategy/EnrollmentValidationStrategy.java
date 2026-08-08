package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.common.exception.ServiceException;

/**
 * Strategy interface for validating student enrollment requests.
 */
public interface EnrollmentValidationStrategy {
    void validate(EnrollmentValidationContext context) throws ServiceException;
}
