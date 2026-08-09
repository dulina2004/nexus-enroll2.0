INSERT INTO audit_reports (id, report_type, title, semester, `year`, generated_by) VALUES
(1, 'ENROLLMENT_SUMMARY', 'Initial Enrollment Report - FALL 2025', 'FALL', 2025, 'SYSTEM')
ON DUPLICATE KEY UPDATE title=VALUES(title);
