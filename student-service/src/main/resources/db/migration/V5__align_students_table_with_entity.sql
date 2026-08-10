-- Reconciles `students` with the Student.java entity, which reads a
-- completely different set of columns (student_id, status, date_of_birth,
-- gender, address, city, state, postal_code, country,
-- emergency_contact_name/phone, graduation_date, total_credits_earned)
-- than V1__init_student_schema.sql creates. Until now those columns were
-- created ad-hoc by ddl-auto:update with no constraints/defaults and left
-- unpopulated for seeded rows - confirmed live: the API returned
-- studentId:"" and status:"" for every seeded student even though
-- student_number/enrollment_status (the V1 columns, which the entity does
-- NOT read) were populated correctly.
--
-- This does not touch the original V1 columns (student_number, program,
-- year_level, enrollment_status, expected_graduation) - they're inert as
-- far as the entity is concerned, kept only for backward compatibility.

-- Plain MySQL (unlike MariaDB) has no `ADD COLUMN IF NOT EXISTS` - not
-- needed anyway since this only ever runs once against a fresh volume
-- (see DOCKER.md: always `docker compose down -v` before `up`).
ALTER TABLE students
    ADD COLUMN student_id VARCHAR(50) NULL,
    ADD COLUMN date_of_birth DATE NULL,
    ADD COLUMN gender VARCHAR(20) NULL,
    ADD COLUMN address TEXT NULL,
    ADD COLUMN city VARCHAR(100) NULL,
    ADD COLUMN state VARCHAR(100) NULL,
    ADD COLUMN postal_code VARCHAR(20) NULL,
    ADD COLUMN country VARCHAR(100) NULL,
    ADD COLUMN emergency_contact_name VARCHAR(100) NULL,
    ADD COLUMN emergency_contact_phone VARCHAR(20) NULL,
    ADD COLUMN graduation_date DATE NULL,
    ADD COLUMN status VARCHAR(50) NULL,
    ADD COLUMN total_credits_earned INT NULL;

UPDATE students SET student_id = student_number WHERE student_id IS NULL OR student_id = '';
UPDATE students SET status = enrollment_status WHERE status IS NULL OR status = '';
UPDATE students SET total_credits_earned = 0 WHERE total_credits_earned IS NULL;

ALTER TABLE students
    MODIFY COLUMN student_id VARCHAR(50) NOT NULL,
    MODIFY COLUMN status VARCHAR(50) NOT NULL,
    ADD UNIQUE KEY uq_stu_student_id (student_id);
