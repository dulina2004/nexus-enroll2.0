package com.nexusenroll.reporting.client;

import java.util.Map;

public interface EnrollmentClient {
    Map<String, Object> getEnrollmentSummary(String semester, int year);
}
