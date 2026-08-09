DROP TABLE IF EXISTS course_change_requests;

CREATE TABLE course_change_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    section_id BIGINT NULL,
    requested_by BIGINT NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    current_value TEXT NULL,
    proposed_value TEXT NOT NULL,
    justification TEXT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT NULL,
    reviewed_at TIMESTAMP NULL,
    review_comment TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ccr_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
