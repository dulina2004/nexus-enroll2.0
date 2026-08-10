-- student_id references student-service `students.id` (1-4), never the
-- auth-service user_id. section_id references course-service
-- `course_sections.id` (1-7, all FALL 2025 - see course-service V2 seed).
-- student1 (student_id=1) gets the richest data: two completed, graded
-- sections plus two currently in-progress enrollments.
INSERT INTO enrollments (id, student_id, section_id, enrollment_date, status, grade, grade_points, credits_earned) VALUES
(1, 1, 1, '2025-08-15', 'COMPLETED', 'A',  4.00, 3),
(2, 1, 3, '2025-08-15', 'COMPLETED', 'B+', 3.30, 4),
(3, 1, 4, '2025-08-15', 'ENROLLED',  NULL, NULL, NULL),
(4, 1, 7, '2025-08-15', 'ENROLLED',  NULL, NULL, NULL),
(5, 2, 5, '2025-08-16', 'ENROLLED',  NULL, NULL, NULL),
(6, 2, 6, '2025-08-16', 'ENROLLED',  NULL, NULL, NULL),
(7, 3, 1, '2025-08-17', 'ENROLLED',  NULL, NULL, NULL),
(8, 4, 2, '2025-08-18', 'ENROLLED',  NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE status=VALUES(status);
