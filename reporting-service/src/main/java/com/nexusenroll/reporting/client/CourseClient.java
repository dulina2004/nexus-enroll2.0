package com.nexusenroll.reporting.client;

import java.util.List;
import java.util.Map;

/**
 * Client abstraction for retrieving course data from the Course Service, used by
 * {@link com.nexusenroll.reporting.service.ReportingDataFetcher} to build reports.
 */
public interface CourseClient {
    List<Map<String, Object>> getCourses();
}
