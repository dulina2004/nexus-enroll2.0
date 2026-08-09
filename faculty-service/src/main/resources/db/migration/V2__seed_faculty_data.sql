INSERT INTO faculty (id, user_id, faculty_id, title, department_id, office_location, office_phone, degree_level, years_experience, employment_type, status, hire_date) VALUES
(1, 2, 'FAC-001', 'Associate Professor', 1, 'Tech Hall 101', '555-0101', 'PhD', 10, 'FULL_TIME', 'ACTIVE', '2014-08-01'),
(2, 3, 'FAC-002', 'Professor',           2, 'Science Bldg 204', '555-0102', 'PhD', 25, 'FULL_TIME', 'ACTIVE', '2019-08-01')
ON DUPLICATE KEY UPDATE faculty_id=VALUES(faculty_id);

INSERT INTO grades (id, enrollment_id, student_id, section_id, assignment_title, points_earned, max_points, letter_grade, graded_by, status, comments) VALUES
(1, 1, 6, 1, 'Final Grade', 95.0, 100.0, 'A',  'FACULTY', 'DRAFT', 'Great work this semester!'),
(2, 2, 6, 5, 'Final Grade', 88.0, 100.0, 'B+', 'FACULTY', 'PENDING', 'Good effort on the final exam.')
ON DUPLICATE KEY UPDATE status=VALUES(status);
