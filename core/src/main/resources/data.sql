-- Initial users for testing
-- Passwords are all 'secret' encoded with BCrypt

INSERT INTO chat_user (id, username, password, bio) VALUES
(1, 'guest', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'Guest user'),
(2, 'user', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'Regular user'),
(3, 'admin', '$2a$10$hg4LTs6JzZSGdxxBetz14.88Jg8X7EmZgiI1Qo1hhw4Ppcex9KJta', 'Administrator');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'GUEST'),
(2, 'USER'),
(2, 'GUEST'),
(3, 'ADMIN'),
(3, 'USER'),
(3, 'GUEST');
