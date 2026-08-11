package com.nexusenroll.reporting.client;

import java.util.List;
import java.util.Map;

/**
 * Client abstraction for retrieving faculty workload data from the Faculty Service, used by
 * {@link com.nexusenroll.reporting.service.ReportingDataFetcher} to build reports.
 */
public interface FacultyClient {
    List<Map<String, Object>> getFacultyWorkload(String semester, int year);
}
