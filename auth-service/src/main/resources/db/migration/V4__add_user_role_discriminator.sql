ALTER TABLE users
    ADD COLUMN role_discriminator VARCHAR(50) NOT NULL DEFAULT 'STUDENT';

UPDATE users
SET role_discriminator = role;
