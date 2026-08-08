package com.nexusenroll.student.service;

import com.nexusenroll.student.dto.SectionDetailsDTO;

import java.util.List;
import java.util.Map;

/**
 * Service client interface for fetching section details from course-service.
 */
public interface CourseServiceClient {
    SectionDetailsDTO getSectionDetails(Long sectionId);
    Map<Long, SectionDetailsDTO> getSectionDetailsMap(List<Long> sectionIds);
}
