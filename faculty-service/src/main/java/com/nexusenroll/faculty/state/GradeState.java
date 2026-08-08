package com.nexusenroll.faculty.state;

import com.nexusenroll.common.exception.ValidationException;

/**
 * State interface for Grade lifecycle in the State Pattern.
 * Workflow: Draft -> Pending -> Approved (or Pending -> Draft on rejection)
 */
public interface GradeState {
    void submit(GradeContext context) throws ValidationException;
    void approve(GradeContext context) throws ValidationException;
    void reject(GradeContext context) throws ValidationException;
    String getStatusName();
    boolean canEdit();
}
