package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;

/**
 * PendingState represents a grade submitted by faculty and awaiting final approval.
 * Transitions: Pending -> Approved (on approve), Pending -> Draft (on reject)
 */
public class PendingState implements GradeState {

    @Override
    public void submit(GradeContext context) throws ValidationException {
        throw new ValidationException("Grade is already submitted and pending approval");
    }

    @Override
    public void approve(GradeContext context) throws ValidationException {
        context.setState(new ApprovedState());
    }

    @Override
    public void reject(GradeContext context) throws ValidationException {
        context.setState(new DraftState());
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }

    @Override
    public boolean canEdit() {
        return false;
    }
}
