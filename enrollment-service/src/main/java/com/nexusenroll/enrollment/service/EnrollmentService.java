package com.nexusenroll.enrollment.service;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.facade.EnrollmentFacade;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service delegation layer wrapping EnrollmentFacade operations.
 */
@Service
public class EnrollmentService {

    private final EnrollmentFacade enrollmentFacade;

    public EnrollmentService(EnrollmentFacade enrollmentFacade) {
        this.enrollmentFacade = enrollmentFacade;
    }

    public Enrollment enroll(long studentId, long sectionId) throws ServiceException {
        return enrollmentFacade.enroll(studentId, sectionId);
    }

    public Enrollment enrollWithOverride(long studentId, long sectionId, Long adminUserId, String reason) throws ServiceException {
        return enrollmentFacade.enrollWithOverride(studentId, sectionId, adminUserId, reason);
    }

    public Enrollment drop(long enrollmentId) throws ServiceException {
        return enrollmentFacade.drop(enrollmentId);
    }

    public List<Enrollment> getEnrollmentsForStudent(long studentId) throws ServiceException {
        return enrollmentFacade.getEnrollmentsForStudent(studentId);
    }

    public WaitlistEntry joinWaitlist(long studentId, long sectionId) throws ServiceException {
        return enrollmentFacade.joinWaitlist(studentId, sectionId);
    }

    public void cancelWaitlist(long waitlistId) throws ServiceException {
        enrollmentFacade.cancelWaitlist(waitlistId);
    }

    public List<WaitlistEntry> getWaitlistForStudent(long studentId) throws ServiceException {
        return enrollmentFacade.getWaitlistForStudent(studentId);
    }
}
