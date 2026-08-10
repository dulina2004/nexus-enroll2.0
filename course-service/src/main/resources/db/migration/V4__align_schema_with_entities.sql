-- Reconciles the schema with the JPA entities so ddl-auto can move from
-- `update` to `validate` (see Course.java / CourseSection.java / DegreeProgram.java,
-- which declare wider varchar columns / an extra column than V1 created).
ALTER TABLE courses MODIFY COLUMN level VARCHAR(50) DEFAULT '100';
ALTER TABLE courses MODIFY COLUMN status VARCHAR(50) DEFAULT 'ACTIVE';
ALTER TABLE course_sections MODIFY COLUMN status VARCHAR(50) DEFAULT 'ACTIVE';
ALTER TABLE degree_programs ADD COLUMN status VARCHAR(50) DEFAULT 'ACTIVE';
