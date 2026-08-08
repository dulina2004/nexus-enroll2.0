package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import com.nexusenroll.enrollment.model.Enrollment;
import com.nexusenroll.enrollment.model.WaitlistEntry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of NotificationClient for event publishing.
 */
@Component
@ConditionalOnMissingBean(name = "customNotificationClient")
public class DefaultNotificationClient implements NotificationClient {

    @Override
    public void publishEnrollmentCreated(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException {
        // Log/publish event
    }

    @Override
    public void publishEnrollmentDropped(Enrollment enrollment, CourseSectionSnapshot section) throws ServiceException {
        // Log/publish event
    }

    @Override
    public void publishWaitlistJoined(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException {
        // Log/publish event
    }

    @Override
    public void publishWaitlistSeatAvailable(WaitlistEntry entry, CourseSectionSnapshot section) throws ServiceException {
        // Log/publish event
    }
}
