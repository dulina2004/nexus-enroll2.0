-- Initialize microservice databases
CREATE DATABASE IF NOT EXISTS nexus_auth;
CREATE DATABASE IF NOT EXISTS nexus_student;
CREATE DATABASE IF NOT EXISTS nexus_course;
CREATE DATABASE IF NOT EXISTS nexus_enrollment;
CREATE DATABASE IF NOT EXISTS nexus_faculty;
CREATE DATABASE IF NOT EXISTS nexus_academic;
CREATE DATABASE IF NOT EXISTS nexus_notification;
CREATE DATABASE IF NOT EXISTS nexus_reporting;

-- Grant privileges to root
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
FLUSH PRIVILEGES;
