package com.nexusenroll.reporting.client;

import java.util.Map;

/**
 * Client abstraction for retrieving enrollment summary data from the Enrollment Service, used
 * by {@link com.nexusenroll.reporting.service.ReportingDataFetcher} to build reports.
 */
public interface EnrollmentClient {
    Map<String, Object> getEnrollmentSummary(String semester, int year);
}
