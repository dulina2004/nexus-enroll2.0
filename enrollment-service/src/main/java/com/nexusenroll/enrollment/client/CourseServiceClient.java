package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;

import java.util.List;

public interface CourseServiceClient {

    CourseSectionSnapshot getSectionSnapshot(long sectionId) throws ServiceException;

    List<CourseSectionSnapshot> getSectionSnapshots(List<Long> sectionIds) throws ServiceException;

    void reserveSeat(long sectionId) throws ServiceException;

    void releaseSeat(long sectionId) throws ServiceException;
}
