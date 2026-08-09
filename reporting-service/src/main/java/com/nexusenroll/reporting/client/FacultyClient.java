package com.nexusenroll.reporting.client;

import java.util.List;
import java.util.Map;

public interface FacultyClient {
    List<Map<String, Object>> getFacultyWorkload(String semester, int year);
}
