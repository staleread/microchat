-- Initial users for testing
-- Passwords are all 'secret' encoded with BCrypt

INSERT INTO chat_user (id, username, password, first_name, last_name, department, bio) VALUES
(1, 'student', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'John', 'Doe', 'Computer Science', 'A student'),
(2, 'professor', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'Jane', 'Smith', 'Computer Science', 'A professor'),
(3, 'admin', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'Admin', 'User', null, 'Administrator');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'STUDENT'),
(2, 'PROFESSOR'),
(3, 'ADMIN');
