INSERT INTO enrollments (id, student_id, section_id, enrollment_date, status) VALUES
(1, 6, 1, '2025-08-15', 'ENROLLED'),
(2, 6, 5, '2025-08-15', 'ENROLLED'),
(3, 7, 6, '2025-08-16', 'ENROLLED'),
(4, 7, 7, '2025-08-16', 'ENROLLED'),
(5, 8, 7, '2025-08-17', 'ENROLLED'),
(6, 8, 3, '2025-08-17', 'ENROLLED')
ON DUPLICATE KEY UPDATE status=VALUES(status);
