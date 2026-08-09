INSERT INTO academic_history (id, student_id, course_code, course_title, credits, grade, semester, `year`) VALUES
(1, 6, 'ENG-101', 'Freshman English', 3, 'A',  'SPRING', 2025),
(2, 6, 'HIST-101', 'World History',    3, 'B+', 'SPRING', 2025)
ON DUPLICATE KEY UPDATE course_code=VALUES(course_code);

INSERT INTO degree_progress (id, student_id, program_id, total_credits_required, total_credits_completed, progress_percentage) VALUES
(1, 6, 1, 120, 6, 5.0),
(2, 7, 1, 120, 15, 12.5),
(3, 8, 1, 120, 90, 75.0),
(4, 9, 1, 120, 110, 91.6)
ON DUPLICATE KEY UPDATE total_credits_completed=VALUES(total_credits_completed);

INSERT INTO cumulative_records (id, student_id, cumulative_gpa, total_credits_earned) VALUES
(1, 6, 3.85, 6),
(2, 7, 3.20, 15),
(3, 8, 4.00, 90),
(4, 9, 3.50, 110)
ON DUPLICATE KEY UPDATE cumulative_gpa=VALUES(cumulative_gpa);
