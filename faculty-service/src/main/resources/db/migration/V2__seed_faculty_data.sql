-- faculty.user_id references auth-service `users.id` (faculty1=2, faculty2=3).
-- Matches the instructor_id convention used in course-service's course_sections.
INSERT INTO faculty (id, user_id, faculty_id, title, department_id, office_location, office_phone, degree_level, years_experience, employment_type, status, hire_date) VALUES
(1, 2, 'FAC-001', 'Associate Professor', 1, 'Tech Hall 101', '555-0101', 'PhD', 10, 'FULL_TIME', 'ACTIVE', '2014-08-01'),
(2, 3, 'FAC-002', 'Professor',           2, 'Science Bldg 204', '555-0102', 'PhD', 25, 'FULL_TIME', 'ACTIVE', '2019-08-01')
ON DUPLICATE KEY UPDATE faculty_id=VALUES(faculty_id);

-- student_id references student-service `students.id` (1-4). enrollment_id
-- and section_id mirror enrollment-service's V2 seed / course-service's
-- course_sections respectively so a demo grade always traces back to a
-- real enrollment.
INSERT INTO grades (id, enrollment_id, student_id, section_id, assignment_title, points_earned, max_points, letter_grade, graded_by, status, comments) VALUES
(1, 1, 1, 1, 'Final Grade',   95.0, 100.0, 'A',  'faculty1', 'FINAL',   'Excellent work all semester!'),
(2, 2, 1, 3, 'Final Grade',   89.0, 100.0, 'B+', 'faculty1', 'FINAL',   'Solid grasp of data structures.'),
(3, 7, 3, 1, 'Midterm Exam',  78.0, 100.0, 'C+', 'faculty1', 'DRAFT',   'Needs to review recursion before the final.'),
(4, 5, 2, 5, 'Midterm Exam',  91.0, 100.0, 'A-', 'faculty2', 'PENDING', 'Great grasp of calculus concepts.')
ON DUPLICATE KEY UPDATE status=VALUES(status);
