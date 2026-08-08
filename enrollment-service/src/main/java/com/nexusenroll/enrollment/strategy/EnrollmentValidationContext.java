package com.nexusenroll.enrollment.strategy;

import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;

import java.util.List;

/**
 * Context object carrying enrollment parameters for strategy validation.
 */
public class EnrollmentValidationContext {
    private final Enrollment enrollment;
    private final CourseSectionSnapshot requestedSection;
    private final List<CourseSectionSnapshot> currentSections;
    private final List<String> completedCourseCodes;

    public EnrollmentValidationContext(Enrollment enrollment,
                                       CourseSectionSnapshot requestedSection,
                                       List<CourseSectionSnapshot> currentSections,
                                       List<String> completedCourseCodes) {
        this.enrollment = enrollment;
        this.requestedSection = requestedSection;
        this.currentSections = currentSections;
        this.completedCourseCodes = completedCourseCodes;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public CourseSectionSnapshot getRequestedSection() {
        return requestedSection;
    }

    public List<CourseSectionSnapshot> getCurrentSections() {
        return currentSections;
    }

    public List<String> getCompletedCourseCodes() {
        return completedCourseCodes;
    }
}
