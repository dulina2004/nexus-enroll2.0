-- =============================================================================
-- NexusEnroll 2.0 - Complete Multi-Service Database Seed Data
-- =============================================================================
-- Inserts rich demo dataset for all 8 microservices after Flyway migrations.
-- Passwords for all demo accounts are: Password123
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

-- =============================================================================
-- 1. AUTH SERVICE (nexus_auth)
-- =============================================================================
USE nexus_auth;

INSERT INTO roles (id, name, description, is_system_role) VALUES
(1, 'STUDENT', 'Student role with enrollment privileges', TRUE),
(2, 'FACULTY', 'Faculty role with grade management privileges', TRUE),
(3, 'ADMIN',   'Administrator with full system access', TRUE)
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- Password for all accounts: Password123 (bcrypt hash)
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, status) VALUES
(1, 'admin',    'admin@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'System', 'Admin',    'ADMIN',   'ACTIVE'),
(2, 'faculty1', 'faculty@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Sarah',  'Connor',   'FACULTY', 'ACTIVE'),
(3, 'faculty2', 'einstein@nexus.edu', '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Albert', 'Einstein', 'FACULTY', 'ACTIVE'),
(4, 'student1', 'student@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'John',   'Doe',      'STUDENT', 'ACTIVE'),
(5, 'student2', 'jbond@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'James',  'Bond',     'STUDENT', 'ACTIVE'),
(6, 'student3', 'mgarcia@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Maria',  'Garcia',   'STUDENT', 'ACTIVE'),
(7, 'student4', 'kchen@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Kevin',  'Chen',     'STUDENT', 'ACTIVE')
ON DUPLICATE KEY UPDATE email=VALUES(email);


-- =============================================================================
-- 2. COURSE SERVICE (nexus_course)
-- =============================================================================
USE nexus_course;

INSERT INTO departments (id, code, name, description) VALUES
(1, 'COMP', 'Computer Science & Engineering', 'Algorithms, Data Structures, Software Engineering, AI'),
(2, 'MATH', 'Mathematics & Statistics', 'Calculus, Linear Algebra, Probability, Statistics'),
(3, 'PHYS', 'Physics & Applied Sciences', 'Mechanics, Quantum Physics, Thermodynamics, Optics'),
(4, 'BUS',  'Business Administration', 'Corporate Strategy, Finance, Organizational Leadership'),
(5, 'ENG',  'Electrical Engineering', 'Digital Systems, Circuit Design, Microelectronics')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO courses (id, course_code, course_number, title, description, credits, capacity, department_id, level, status) VALUES
(1, 'CS-101',   101, 'Introduction to Computer Science & Programming', 'Fundamental concepts of computer science, algorithms, basic data structures, and Python programming.', 3, 50, 1, '100', 'ACTIVE'),
(2, 'CS-201',   201, 'Data Structures & Algorithms', 'Trees, Graphs, Hash Tables, Dynamic Programming, and complexity analysis.', 4, 40, 1, '200', 'ACTIVE'),
(3, 'CS-401',   401, 'Machine Learning & Neural Networks', 'Supervised learning, deep neural architectures, PyTorch, and AI system engineering.', 3, 30, 1, '400', 'ACTIVE'),
(4, 'MATH-101', 101, 'Calculus I & Analytical Geometry', 'Limits, derivatives, integrals, and mathematical foundations.', 4, 60, 2, '100', 'ACTIVE'),
(5, 'MATH-201', 201, 'Linear Algebra & Matrix Computation', 'Vector spaces, eigenvalues, matrix decompositions, and linear systems.', 3, 45, 2, '200', 'ACTIVE'),
(6, 'PHYS-101', 101, 'General Physics I: Classical Mechanics', 'Newtonian mechanics, rotational dynamics, work, energy, and thermodynamics.', 4, 50, 3, '100', 'ACTIVE')
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO course_sections (id, course_id, section_number, instructor_id, semester, term, year, schedule_days, start_time, end_time, location, capacity, enrolled_count, status) VALUES
(1, 1, '01', 2, 'FALL', 'FALL', 2025, 'MWF', '09:00:00', '10:00:00', 'Science Hall 101', 50, 2, 'ACTIVE'),
(2, 1, '02', 3, 'FALL', 'FALL', 2025, 'TTH', '14:00:00', '15:30:00', 'Science Hall 102', 50, 1, 'ACTIVE'),
(3, 2, '01', 2, 'FALL', 'FALL', 2025, 'MWF', '11:00:00', '12:00:00', 'Turing Lab 204', 40, 1, 'ACTIVE'),
(4, 3, '01', 2, 'FALL', 'FALL', 2025, 'TTH', '10:00:00', '11:30:00', 'AI Research Lab 305', 30, 1, 'ACTIVE'),
(5, 4, '01', 3, 'FALL', 'FALL', 2025, 'MWF', '08:00:00', '09:00:00', 'Euler Hall 105', 60, 1, 'ACTIVE'),
(6, 5, '01', 3, 'FALL', 'FALL', 2025, 'MWF', '13:00:00', '14:00:00', 'Euler Hall 202', 45, 1, 'ACTIVE'),
(7, 6, '01', 2, 'FALL', 'FALL', 2025, 'TTH', '08:30:00', '10:00:00', 'Newton Auditorium', 50, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE section_number=VALUES(section_number);

INSERT INTO degree_programs (id, code, name, department_id, total_credits_required) VALUES
(1, 'BS-CS',   'Bachelor of Science in Computer Science', 1, 120),
(2, 'BS-MATH', 'Bachelor of Science in Mathematics',      2, 120),
(3, 'BS-PHYS', 'Bachelor of Science in Physics',          3, 120)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO program_requirements (id, program_id, course_id, requirement_type, minimum_grade) VALUES
(1, 1, 1, 'MAJOR_REQUIRED', 'C'),
(2, 1, 2, 'MAJOR_REQUIRED', 'C'),
(3, 1, 3, 'MAJOR_ELECTIVE', 'C'),
(4, 1, 4, 'GENERAL_ED',     'C'),
(5, 1, 5, 'MAJOR_REQUIRED', 'C')
ON DUPLICATE KEY UPDATE requirement_type=VALUES(requirement_type);

INSERT INTO course_change_requests (id, course_id, request_type, requested_by, proposed_value, justification, status) VALUES
(1, 1, 'CAPACITY_CHANGE', 2, '60', 'High student demand for introductory programming in Fall 2025', 'PENDING'),
(2, 2, 'CAPACITY_CHANGE', 2, '50', 'Waitlist exceeds 15 students', 'APPROVED'),
(3, 3, 'SCHEDULE_CHANGE', 2, 'MWF 14:00-15:00', 'Resolve room conflict with graduate seminar', 'PENDING')
ON DUPLICATE KEY UPDATE status=VALUES(status);


-- =============================================================================
-- 3. STUDENT SERVICE (nexus_student)
-- =============================================================================
USE nexus_student;

INSERT INTO students (id, user_id, student_id, student_number, program, year_level, gpa, status, enrollment_status, admission_date, expected_graduation) VALUES
(1, 4, 'STU-2024-001', 'STU-2024-001', 'Computer Science', 2, 3.65, 'ACTIVE', 'ENROLLED', '2023-08-01', '2027-05-01'),
(2, 5, 'STU-2024-002', 'STU-2024-002', 'Mathematics',      1, 3.75, 'ACTIVE', 'ENROLLED', '2024-08-01', '2028-05-01'),
(3, 6, 'STU-2023-003', 'STU-2023-003', 'Physics',          3, 3.20, 'ACTIVE', 'ENROLLED', '2022-08-01', '2026-05-01'),
(4, 7, 'STU-2025-004', 'STU-2025-004', 'Computer Science', 1, 3.90, 'ACTIVE', 'ENROLLED', '2025-08-01', '2029-05-01')
ON DUPLICATE KEY UPDATE student_number=VALUES(student_number);


-- =============================================================================
-- 4. ENROLLMENT SERVICE (nexus_enrollment)
-- =============================================================================
USE nexus_enrollment;

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

INSERT INTO waitlist (id, student_id, section_id, position, status) VALUES
(1, 3, 4, 1, 'WAITING')
ON DUPLICATE KEY UPDATE status=VALUES(status);


-- =============================================================================
-- 5. FACULTY SERVICE (nexus_faculty)
-- =============================================================================
USE nexus_faculty;

INSERT INTO faculty (id, user_id, faculty_id, title, department_id, office_location, office_phone, degree_level, years_experience, employment_type, status, hire_date) VALUES
(1, 2, 'FAC-001', 'Associate Professor', 1, 'Tech Hall 101', '555-0101', 'PhD', 10, 'FULL_TIME', 'ACTIVE', '2014-08-01'),
(2, 3, 'FAC-002', 'Professor',           2, 'Science Bldg 204', '555-0102', 'PhD', 25, 'FULL_TIME', 'ACTIVE', '2019-08-01')
ON DUPLICATE KEY UPDATE faculty_id=VALUES(faculty_id);

INSERT INTO grades (id, enrollment_id, student_id, section_id, assignment_title, points_earned, max_points, letter_grade, graded_by, status, comments) VALUES
(1, 1, 1, 1, 'Final Grade',   95.0, 100.0, 'A',  'faculty1', 'FINAL',   'Excellent performance and mastery of Python fundamentals.'),
(2, 2, 1, 3, 'Final Grade',   89.0, 100.0, 'B+', 'faculty1', 'FINAL',   'Solid grasp of data structures and algorithms.'),
(3, 7, 3, 1, 'Midterm Exam',  78.0, 100.0, 'C+', 'faculty1', 'DRAFT',   'Review dynamic memory and recursion before final.'),
(4, 5, 2, 5, 'Midterm Exam',  91.0, 100.0, 'A-', 'faculty2', 'PENDING', 'Great understanding of integral calculus applications.')
ON DUPLICATE KEY UPDATE status=VALUES(status);


-- =============================================================================
-- 6. ACADEMIC RECORD SERVICE (nexus_academic_record)
-- =============================================================================
USE nexus_academic_record;

INSERT INTO academic_history (id, student_id, course_code, course_title, credits, grade, semester, `year`) VALUES
(1, 1, 'ENG-101',  'Freshman English',       3, 'A',  'SPRING', 2025),
(2, 1, 'HIST-101', 'World History',          3, 'B+', 'SPRING', 2025),
(3, 1, 'CS-050',   'Intro to Computing Lab', 1, 'A',  'SPRING', 2025),
(4, 2, 'ENG-101',  'Freshman English',       3, 'B',  'SPRING', 2025),
(5, 3, 'ENG-101',  'Freshman English',       3, 'A-', 'SPRING', 2025)
ON DUPLICATE KEY UPDATE course_code=VALUES(course_code);

INSERT INTO degree_progress (id, student_id, program_id, total_credits_required, total_credits_completed, general_ed_completed, major_credits_completed, elective_credits_completed, progress_percentage, expected_graduation_date) VALUES
(1, 1, 1, 120, 34, 12, 18, 4, 28.3, '2027-05-01'),
(2, 2, 2, 120, 15, 9,  6,  0, 12.5, '2028-05-01'),
(3, 3, 3, 120, 78, 30, 42, 6, 65.0, '2026-05-01'),
(4, 4, 1, 120, 3,  3,  0,  0, 2.5,  '2029-05-01')
ON DUPLICATE KEY UPDATE total_credits_completed=VALUES(total_credits_completed);

INSERT INTO cumulative_records (id, student_id, cumulative_gpa, total_credits_earned, total_credits_attempted, total_credits_passed, total_credits_failed, graduation_status) VALUES
(1, 1, 3.65, 34, 34, 34, 0, 'IN_PROGRESS'),
(2, 2, 3.75, 15, 15, 15, 0, 'IN_PROGRESS'),
(3, 3, 3.10, 78, 82, 78, 4, 'IN_PROGRESS'),
(4, 4, 3.90, 3,  3,  3,  0, 'IN_PROGRESS')
ON DUPLICATE KEY UPDATE cumulative_gpa=VALUES(cumulative_gpa);


-- =============================================================================
-- 7. NOTIFICATION SERVICE (nexus_notification)
-- =============================================================================
USE nexus_notification;

INSERT INTO notifications (id, recipient_user_id, title, message, notification_type, priority, related_entity_type, related_entity_id, is_read) VALUES
(1, 4, 'Welcome to NexusEnroll', 'Welcome to the new academic portal!', 'SYSTEM', 'MEDIUM', 'USER', 4, 0),
(2, 4, 'Enrollment Confirmation', 'Successfully enrolled in CS-101 Section 01', 'ENROLLMENT', 'HIGH', 'ENROLLMENT', 1, 0),
(3, 4, 'Grade Posted', 'Your final grade for CS-101 has been posted: A', 'GRADE', 'HIGH', 'GRADE', 1, 0),
(4, 5, 'Welcome to NexusEnroll', 'Welcome to the new academic portal!', 'SYSTEM', 'MEDIUM', 'USER', 5, 0),
(5, 2, 'New Enrollment in Your Section', 'A new student has enrolled in CS-101 Section 01', 'ENROLLMENT', 'MEDIUM', 'ENROLLMENT', 1, 0),
(6, 1, 'System Maintenance Notice', 'Scheduled maintenance this weekend.', 'SYSTEM', 'LOW', 'SYSTEM', NULL, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);


-- =============================================================================
-- 8. REPORTING SERVICE (nexus_reporting)
-- =============================================================================
USE nexus_reporting;

INSERT INTO audit_reports (id, report_type, title, semester, `year`, generated_by) VALUES
(1, 'ENROLLMENT_SUMMARY', 'Initial Enrollment Report - FALL 2025', 'FALL', 2025, 'SYSTEM'),
(2, 'COURSE_POPULARITY',  'Course Capacity & Fill Rates - FALL 2025', 'FALL', 2025, 'SYSTEM'),
(3, 'FACULTY_WORKLOAD',   'Faculty Section Teaching Allocations - FALL 2025', 'FALL', 2025, 'SYSTEM')
ON DUPLICATE KEY UPDATE title=VALUES(title);

SET FOREIGN_KEY_CHECKS = 1;
