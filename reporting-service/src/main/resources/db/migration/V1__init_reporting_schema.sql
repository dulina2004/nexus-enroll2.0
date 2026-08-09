CREATE TABLE IF NOT EXISTS audit_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_type VARCHAR(50) NOT NULL,
    title VARCHAR(150),
    semester VARCHAR(20),
    `year` INT,
    report_data TEXT,
    generated_by VARCHAR(50),
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rpt_report_type (report_type),
    INDEX idx_rpt_generated_at (generated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
