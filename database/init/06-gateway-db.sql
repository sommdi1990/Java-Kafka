-- Flyway migration for gateway database
USE
gateway_db;

-- Users table
CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    username
    VARCHAR
(
    50
) NOT NULL UNIQUE,
    email VARCHAR
(
    100
) NOT NULL UNIQUE,
    password VARCHAR
(
    200
) NOT NULL,
    first_name VARCHAR
(
    50
),
    last_name VARCHAR
(
    50
),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts INT DEFAULT 0,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username
(
    username
),
    INDEX idx_email
(
    email
),
    INDEX idx_is_active
(
    is_active
)
    );

-- User roles table
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id
    BIGINT
    NOT
    NULL,
    role
    ENUM
(
    'ADMIN',
    'USER',
    'OPERATOR',
    'VIEWER'
) NOT NULL,
    PRIMARY KEY
(
    user_id,
    role
),
    FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE
    );

-- Sample users
INSERT INTO users (username, email, password, first_name, last_name, is_active)
VALUES ('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHhN.8x0/8MZ8Q8Q8Q8Q8Q8Q', 'Admin', 'User',
        TRUE),
       ('user', 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHhN.8x0/8MZ8Q8Q8Q8Q8Q8Q', 'Regular', 'User',
        TRUE);

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ADMIN'),
       (2, 'USER');
