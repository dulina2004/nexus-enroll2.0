INSERT INTO notifications (id, recipient_user_id, title, message, notification_type, priority, related_entity_type, related_entity_id, is_read) VALUES
(1, 4, 'Welcome to NexusEnroll', 'Welcome to the new academic portal!', 'SYSTEM', 'MEDIUM', 'USER', 4, 0),
(2, 4, 'Enrollment Confirmation', 'Successfully enrolled in CS-101 Section 01', 'ENROLLMENT', 'HIGH', 'ENROLLMENT', 1, 0)
ON DUPLICATE KEY UPDATE title=VALUES(title);
