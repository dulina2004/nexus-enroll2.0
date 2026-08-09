INSERT INTO students (id, user_id, student_number, program, year_level, gpa, enrollment_status, admission_date, expected_graduation) VALUES
(1, 4, 'STU-2024-001', 'Computer Science', 2, 3.50, 'ENROLLED', '2023-08-01', '2027-05-01'),
(2, 5, 'STU-2024-002', 'Mathematics',      1, 3.75, 'ENROLLED', '2024-08-01', '2028-05-01')
ON DUPLICATE KEY UPDATE student_number=VALUES(student_number);
