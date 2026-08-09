CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipient_user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT,
    notification_type VARCHAR(50),
    priority VARCHAR(20),
    related_entity_type VARCHAR(50),
    related_entity_id BIGINT,
    is_read BIT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ntf_recipient_user_id (recipient_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS emails (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipient_email VARCHAR(150) NOT NULL,
    recipient_user_id BIGINT,
    subject VARCHAR(150),
    body TEXT,
    email_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    INDEX idx_eml_recipient_email (recipient_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
