package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;

import java.util.List;

/**
 * Abstraction over the remote Course Service, providing course section data and
 * seat reservation/release operations used during enrollment and waitlist processing.
 */
public interface CourseServiceClient {

    CourseSectionSnapshot getSectionSnapshot(long sectionId) throws ServiceException;

    List<CourseSectionSnapshot> getSectionSnapshots(List<Long> sectionIds) throws ServiceException;

    void reserveSeat(long sectionId) throws ServiceException;

    void releaseSeat(long sectionId) throws ServiceException;
}
