-- recipient_user_id references auth-service `users.id` directly (notifications
-- go to a login identity, not a student/faculty record), so admin (1) and
-- faculty1 (2) get notifications too, not just students.
INSERT INTO notifications (id, recipient_user_id, title, message, notification_type, priority, related_entity_type, related_entity_id, is_read) VALUES
(1, 4, 'Welcome to NexusEnroll', 'Welcome to the new academic portal!', 'SYSTEM', 'MEDIUM', 'USER', 4, 0),
(2, 4, 'Enrollment Confirmation', 'Successfully enrolled in CS-101 Section 01', 'ENROLLMENT', 'HIGH', 'ENROLLMENT', 1, 0),
(3, 4, 'Grade Posted', 'Your final grade for CS-101 has been posted: A', 'GRADE', 'HIGH', 'GRADE', 1, 0),
(4, 5, 'Welcome to NexusEnroll', 'Welcome to the new academic portal!', 'SYSTEM', 'MEDIUM', 'USER', 5, 0),
(5, 2, 'New Enrollment in Your Section', 'A new student has enrolled in CS-101 Section 01', 'ENROLLMENT', 'MEDIUM', 'ENROLLMENT', 1, 0),
(6, 1, 'System Maintenance Notice', 'Scheduled maintenance this weekend.', 'SYSTEM', 'LOW', 'SYSTEM', NULL, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);
