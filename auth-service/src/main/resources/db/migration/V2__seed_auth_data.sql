-- Canonical demo dataset. Every seeded user shares the password "Password123";
-- the hash below is a real bcrypt hash (verified against BCryptPasswordEncoder,
-- see AuthServiceSeedPasswordTest), not a hand-typed placeholder.
-- auth `users.id` is the ID every other service's *_service seeds key off of
-- (student.user_id, faculty.user_id, notification.recipient_user_id).

INSERT INTO roles (id, name, description, is_system_role) VALUES
(1, 'STUDENT', 'Student role with enrollment privileges', TRUE),
(2, 'FACULTY', 'Faculty role with grade management privileges', TRUE),
(3, 'ADMIN',   'Administrator with full system access', TRUE)
ON DUPLICATE KEY UPDATE description=VALUES(description);

INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, status) VALUES
(1, 'admin',    'admin@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'System', 'Admin',    'ADMIN',   'ACTIVE'),
(2, 'faculty1', 'faculty@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Sarah',  'Connor',   'FACULTY', 'ACTIVE'),
(3, 'faculty2', 'einstein@nexus.edu', '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Albert', 'Einstein', 'FACULTY', 'ACTIVE'),
(4, 'student1', 'student@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'John',   'Doe',      'STUDENT', 'ACTIVE'),
(5, 'student2', 'jbond@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'James',  'Bond',     'STUDENT', 'ACTIVE'),
(6, 'student3', 'mgarcia@nexus.edu',  '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Maria',  'Garcia',   'STUDENT', 'ACTIVE'),
(7, 'student4', 'kchen@nexus.edu',    '$2a$10$3dTY3n32VEXd5nBWfG15xO4qY4JxwzRJGkOOKxApLERtIdlAU2Z/G', 'Kevin',  'Chen',     'STUDENT', 'ACTIVE')
ON DUPLICATE KEY UPDATE email=VALUES(email);
