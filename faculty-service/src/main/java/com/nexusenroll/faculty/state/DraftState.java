package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;

/**
 * DraftState represents an initial grade draft.
 * Transition: Draft -> Pending (on submit)
 */
public class DraftState implements GradeState {

    @Override
    public void submit(GradeContext context) throws ValidationException {
        if (context.getPointsEarned() < 0) {
            throw new ValidationException("Points earned cannot be negative");
        }
        if (context.getMaxPoints() <= 0) {
            throw new ValidationException("Max points must be greater than zero");
        }
        context.setState(new PendingState());
    }

    @Override
    public void approve(GradeContext context) throws ValidationException {
        throw new ValidationException("Grade must be submitted to Pending state before approval");
    }

    @Override
    public void reject(GradeContext context) throws ValidationException {
        throw new ValidationException("Grade in Draft state cannot be rejected");
    }

    @Override
    public String getStatusName() {
        return "DRAFT";
    }

    @Override
    public boolean canEdit() {
        return true;
    }
}
