-- student_id references student-service `students.id` (1-4), same
-- convention as enrollment-service and faculty-service grades.
INSERT INTO academic_history (id, student_id, course_code, course_title, credits, grade, semester, `year`) VALUES
(1, 1, 'ENG-101',  'Freshman English',       3, 'A',  'SPRING', 2025),
(2, 1, 'HIST-101', 'World History',          3, 'B+', 'SPRING', 2025),
(3, 1, 'CS-050',   'Intro to Computing Lab', 1, 'A',  'SPRING', 2025),
(4, 2, 'ENG-101',  'Freshman English',       3, 'B',  'SPRING', 2025),
(5, 3, 'ENG-101',  'Freshman English',       3, 'A-', 'SPRING', 2025)
ON DUPLICATE KEY UPDATE course_code=VALUES(course_code);

-- student1 (id=1) is the richest profile: 2nd-year CS student with the most
-- credits completed, matching the enrollment-service and faculty-service seeds.
INSERT INTO degree_progress (id, student_id, program_id, total_credits_required, total_credits_completed, general_ed_completed, major_credits_completed, elective_credits_completed, progress_percentage, expected_graduation_date) VALUES
(1, 1, 1, 120, 34, 12,  18, 4, 28.3, '2027-05-01'),
(2, 2, 2, 120, 15, 9,   6,  0, 12.5, '2028-05-01'),
(3, 3, 3, 120, 78, 30,  42, 6, 65.0, '2026-05-01'),
(4, 4, 1, 120, 3,  3,   0,  0, 2.5,  '2029-05-01')
ON DUPLICATE KEY UPDATE total_credits_completed=VALUES(total_credits_completed);

INSERT INTO cumulative_records (id, student_id, cumulative_gpa, total_credits_earned, total_credits_attempted, total_credits_passed, total_credits_failed, graduation_status) VALUES
(1, 1, 3.65, 34, 34, 34, 0, 'IN_PROGRESS'),
(2, 2, 3.75, 15, 15, 15, 0, 'IN_PROGRESS'),
(3, 3, 3.10, 78, 82, 78, 4, 'IN_PROGRESS'),
(4, 4, 3.90, 3,  3,  3,  0, 'IN_PROGRESS')
ON DUPLICATE KEY UPDATE cumulative_gpa=VALUES(cumulative_gpa);
