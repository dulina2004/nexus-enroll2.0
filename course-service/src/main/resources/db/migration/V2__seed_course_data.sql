INSERT INTO departments (id, code, name, description) VALUES
(1, 'COMP', 'Computer Science', 'Algorithms, Data Structures, Software Engineering'),
(2, 'MATH', 'Mathematics', 'Calculus, Algebra, Statistics'),
(3, 'PHYS', 'Physics', 'Mechanics, Quantum Physics, Thermodynamics')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO courses (id, course_code, course_number, title, description, credits, capacity, department_id, level, status) VALUES
(1, 'CS-101', 101, 'Intro to Programming', 'Learn Python and basics of programming', 3, 50, 1, '100', 'ACTIVE'),
(2, 'CS-201', 201, 'Data Structures', 'Trees, Graphs, Hash Tables', 4, 40, 1, '200', 'ACTIVE'),
(3, 'CS-401', 401, 'Machine Learning', 'Intro to Neural Networks', 3, 30, 1, '400', 'ACTIVE'),
(4, 'MATH-101', 101, 'Calculus I', 'Limits, Derivatives', 4, 60, 2, '100', 'ACTIVE'),
(5, 'MATH-201', 201, 'Linear Algebra', 'Vectors, Matrices', 3, 45, 2, '200', 'ACTIVE'),
(6, 'PHYS-101', 101, 'General Physics I', 'Mechanics', 4, 50, 3, '100', 'ACTIVE')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- instructor_id references auth-service `users.id` (== faculty.user_id in
-- faculty-service), not faculty-service's local `faculty.id`. There is no
-- FK across services to enforce this, so it's load-bearing only by
-- convention: faculty1 = user 2 (Sarah Connor), faculty2 = user 3 (Einstein).
-- enrolled_count is kept in sync with enrollment-service's V2 seed below.
INSERT INTO course_sections (id, course_id, section_number, instructor_id, semester, term, year, schedule_days, start_time, end_time, capacity, enrolled_count, status) VALUES
(1, 1, '01', 2, 'FALL', 'FALL', 2025, 'MWF', '09:00:00', '10:00:00', 50, 2, 'ACTIVE'),
(2, 1, '02', 3, 'FALL', 'FALL', 2025, 'TTH', '14:00:00', '15:30:00', 50, 1, 'ACTIVE'),
(3, 2, '01', 2, 'FALL', 'FALL', 2025, 'MWF', '11:00:00', '12:00:00', 40, 1, 'ACTIVE'),
(4, 3, '01', 2, 'FALL', 'FALL', 2025, 'TTH', '10:00:00', '11:30:00', 30, 1, 'ACTIVE'),
(5, 4, '01', 3, 'FALL', 'FALL', 2025, 'MWF', '08:00:00', '09:00:00', 60, 1, 'ACTIVE'),
(6, 5, '01', 3, 'FALL', 'FALL', 2025, 'MWF', '13:00:00', '14:00:00', 45, 1, 'ACTIVE'),
(7, 6, '01', 2, 'FALL', 'FALL', 2025, 'TTH', '08:30:00', '10:00:00', 50, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE section_number=VALUES(section_number);
