-- Makes table creation reproducible via Flyway instead of relying on
-- ddl-auto:update. NOTE: ddl-auto stays `update` for this service (see
-- application.yaml) because the Student entity references ~11 columns
-- (student_id, date_of_birth, gender, address, status, total_credits_earned,
-- etc.) that do not exist in V1__init_student_schema.sql at all - that
-- divergence needs a dedicated migration + data decision (keep/rename/drop
-- the V1 columns it doesn't use: student_number, program, year_level,
-- enrollment_status, expected_graduation) before `validate` is safe here.

CREATE TABLE IF NOT EXISTS student_documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    document_type VARCHAR(100),
    file_path VARCHAR(500),
    upload_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_sdc_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student_programs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    program_code VARCHAR(50) NOT NULL,
    program_name VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_spg_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
