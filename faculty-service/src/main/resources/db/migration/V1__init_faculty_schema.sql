CREATE TABLE IF NOT EXISTS faculty (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE,
    faculty_id VARCHAR(50) UNIQUE,
    title VARCHAR(100),
    department_id BIGINT,
    office_location VARCHAR(100),
    office_phone VARCHAR(20),
    research_interests TEXT,
    bio TEXT,
    degree_level VARCHAR(50),
    years_experience INT DEFAULT 0,
    employment_type VARCHAR(50) DEFAULT 'FULL_TIME',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    hire_date DATE,
    INDEX idx_fac_user_id (user_id),
    INDEX idx_fac_faculty_id (faculty_id),
    INDEX idx_fac_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS grades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id BIGINT NOT NULL,
    student_id BIGINT,
    section_id BIGINT,
    assignment_title VARCHAR(150),
    max_points DOUBLE,
    points_earned DOUBLE,
    letter_grade VARCHAR(10),
    comments TEXT,
    graded_by VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    INDEX idx_grd_enrollment_id (enrollment_id),
    INDEX idx_grd_student_id (student_id),
    INDEX idx_grd_section_id (section_id),
    INDEX idx_grd_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
