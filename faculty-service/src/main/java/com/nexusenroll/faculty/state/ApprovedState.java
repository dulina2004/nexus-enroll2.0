package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;

/**
 * ApprovedState represents a finalized, approved grade.
 * Once approved, a grade is locked and cannot be edited or modified.
 */
public class ApprovedState implements GradeState {

    @Override
    public void submit(GradeContext context) throws ValidationException {
        throw new ValidationException("Grade is already approved and locked");
    }

    @Override
    public void approve(GradeContext context) throws ValidationException {
        throw new ValidationException("Grade is already approved");
    }

    @Override
    public void reject(GradeContext context) throws ValidationException {
        throw new ValidationException("Approved grade cannot be rejected");
    }

    @Override
    public String getStatusName() {
        return "APPROVED";
    }

    @Override
    public boolean canEdit() {
        return false;
    }
}
