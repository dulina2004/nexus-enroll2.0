package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;

/**
 * Abstraction over the remote Notification Service, used to publish enrollment and
 * waitlist lifecycle events to students.
 */
public interface NotificationClient {

    void publishEnrollmentCreated(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException;

    void publishEnrollmentDropped(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException;

    void publishWaitlistJoined(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException;

    void publishWaitlistSeatAvailable(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException;
}
