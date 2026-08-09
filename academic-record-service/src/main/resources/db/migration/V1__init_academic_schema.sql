CREATE TABLE IF NOT EXISTS academic_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    course_title VARCHAR(150),
    credits INT,
    grade VARCHAR(10),
    semester VARCHAR(20),
    `year` INT,
    instructor_name VARCHAR(100),
    INDEX idx_ach_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS grades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id BIGINT NOT NULL,
    student_id BIGINT,
    course_code VARCHAR(20),
    course_title VARCHAR(150),
    assignment_title VARCHAR(150),
    max_points DOUBLE,
    points_earned DOUBLE,
    letter_grade VARCHAR(10),
    graded_by VARCHAR(100),
    graded_date DATE,
    comments TEXT,
    INDEX idx_acg_enrollment_id (enrollment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS degree_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL UNIQUE,
    program_id BIGINT,
    total_credits_required INT,
    total_credits_completed INT,
    general_ed_completed INT,
    major_credits_completed INT,
    elective_credits_completed INT,
    progress_percentage DOUBLE,
    expected_graduation_date DATE,
    INDEX idx_dgp_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cumulative_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL UNIQUE,
    cumulative_gpa DOUBLE,
    total_credits_earned INT,
    total_credits_attempted INT,
    total_credits_passed INT,
    total_credits_failed INT,
    graduation_status VARCHAR(50),
    INDEX idx_cmr_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
