CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    student_number VARCHAR(50) NOT NULL UNIQUE,
    program VARCHAR(100),
    year_level INT DEFAULT 1,
    gpa DECIMAL(3,2) DEFAULT 0.00,
    enrollment_status VARCHAR(50) DEFAULT 'ENROLLED',
    admission_date DATE,
    expected_graduation DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stu_student_number (student_number),
    INDEX idx_stu_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
