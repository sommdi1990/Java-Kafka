-- Flyway migration for CBI database
USE
cbi_db;

-- External services table
CREATE TABLE IF NOT EXISTS external_services
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    100
) NOT NULL UNIQUE,
    description TEXT,
    service_type ENUM
(
    'REST_API',
    'SOAP_WEB_SERVICE',
    'GRAPHQL',
    'FTP',
    'EMAIL'
) NOT NULL,
    endpoint_url VARCHAR
(
    500
) NOT NULL,
    wsdl_url VARCHAR
(
    500
),
    authentication_type ENUM
(
    'NONE',
    'BASIC_AUTH',
    'API_KEY',
    'OAUTH2',
    'JWT',
    'CUSTOM'
),
    username VARCHAR
(
    100
),
    password VARCHAR
(
    200
),
    api_key VARCHAR
(
    200
),
    headers TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    timeout_ms INT DEFAULT 30000,
    retry_count INT DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_service_type
(
    service_type
),
    INDEX idx_is_active
(
    is_active
)
    );

-- Sample external services
INSERT INTO external_services (name, description, service_type, endpoint_url, authentication_type, is_active)
VALUES ('JSONPlaceholder API', 'Sample REST API for testing', 'REST_API', 'https://jsonplaceholder.typicode.com',
        'NONE', TRUE),
       ('Weather API', 'Weather information service', 'REST_API', 'https://api.openweathermap.org/data/2.5', 'API_KEY',
        TRUE),
       ('Sample SOAP Service', 'Sample SOAP web service', 'SOAP_WEB_SERVICE',
        'http://www.dneonline.com/calculator.asmx', 'NONE', TRUE);
