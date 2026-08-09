INSERT INTO roles (id, name, description, is_system_role) VALUES
(1, 'STUDENT', 'Student role with enrollment privileges', TRUE),
(2, 'FACULTY', 'Faculty role with grade management privileges', TRUE),
(3, 'ADMIN',   'Administrator with full system access', TRUE)
ON DUPLICATE KEY UPDATE description=VALUES(description);

INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, status) VALUES
(1, 'admin',    'admin@nexus.edu',    '$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5', 'System', 'Admin',    'ADMIN',   'ACTIVE'),
(2, 'faculty1', 'faculty@nexus.edu',  '$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5', 'Sarah',  'Connor',   'FACULTY', 'ACTIVE'),
(3, 'faculty2', 'einstein@nexus.edu', '$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5', 'Albert', 'Einstein', 'FACULTY', 'ACTIVE'),
(4, 'student1', 'student@nexus.edu',  '$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5', 'John',    'Doe',      'STUDENT', 'ACTIVE'),
(5, 'student2', 'jbond@nexus.edu',    '$2a$10$7R0J4i5B3u8wzL8q9x8E.OqZ2z6z5y.m7N.Q5Z5Q5Z5Q5Z5Q5Z5Q5', 'James',   'Bond',     'STUDENT', 'ACTIVE')
ON DUPLICATE KEY UPDATE email=VALUES(email);
