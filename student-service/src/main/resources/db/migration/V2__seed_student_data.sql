-- students.id is the canonical "student id" used by enrollment-service,
-- academic-record-service, and faculty-service grades (see their V2 seeds).
-- user_id maps 1:1 to auth-service `users.id` (student1..student4 = 4..7).

INSERT INTO students (id, user_id, student_number, program, year_level, gpa, enrollment_status, admission_date, expected_graduation) VALUES
(1, 4, 'STU-2024-001', 'Computer Science', 2, 3.50, 'ENROLLED', '2023-08-01', '2027-05-01'),
(2, 5, 'STU-2024-002', 'Mathematics',      1, 3.75, 'ENROLLED', '2024-08-01', '2028-05-01'),
(3, 6, 'STU-2023-003', 'Physics',          3, 3.20, 'ENROLLED', '2022-08-01', '2026-05-01'),
(4, 7, 'STU-2025-004', 'Computer Science', 1, 3.90, 'ENROLLED', '2025-08-01', '2029-05-01')
ON DUPLICATE KEY UPDATE student_number=VALUES(student_number);
