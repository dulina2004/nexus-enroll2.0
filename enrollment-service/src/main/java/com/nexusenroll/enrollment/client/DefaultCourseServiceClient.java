package com.nexusenroll.enrollment.client;

import com.nexusenroll.common.exception.ResourceNotFoundException;
import com.nexusenroll.common.exception.ServiceException;
import com.nexusenroll.enrollment.model.CourseSectionSnapshot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory / HTTP client fallback implementation of CourseServiceClient.
 */
@Component
@ConditionalOnMissingBean(name = "customCourseServiceClient")
public class DefaultCourseServiceClient implements CourseServiceClient {

    private final Map<Long, CourseSectionSnapshot> sectionStore = new ConcurrentHashMap<>();

    public void registerSection(CourseSectionSnapshot snapshot) {
        sectionStore.put(snapshot.getSectionId(), snapshot);
    }

    @Override
    public CourseSectionSnapshot getSectionSnapshot(long sectionId) throws ServiceException {
        CourseSectionSnapshot snapshot = sectionStore.get(sectionId);
        if (snapshot == null) {
            throw new ResourceNotFoundException("Course section not found: " + sectionId);
        }
        return snapshot;
    }

    @Override
    public List<CourseSectionSnapshot> getSectionSnapshots(List<Long> sectionIds) throws ServiceException {
        List<CourseSectionSnapshot> list = new ArrayList<>();
        if (sectionIds == null || sectionIds.isEmpty()) {
            return list;
        }
        for (Long id : sectionIds) {
            CourseSectionSnapshot snap = sectionStore.get(id);
            if (snap != null) {
                list.add(snap);
            }
        }
        return list;
    }

    @Override
    public void reserveSeat(long sectionId) throws ServiceException {
        CourseSectionSnapshot snapshot = getSectionSnapshot(sectionId);
        if (snapshot.getEnrolledCount() != null) {
            snapshot.setEnrolledCount(snapshot.getEnrolledCount() + 1);
        }
    }

    @Override
    public void releaseSeat(long sectionId) throws ServiceException {
        CourseSectionSnapshot snapshot = sectionStore.get(sectionId);
        if (snapshot != null && snapshot.getEnrolledCount() != null && snapshot.getEnrolledCount() > 0) {
            snapshot.setEnrolledCount(snapshot.getEnrolledCount() - 1);
        }
    }
}
