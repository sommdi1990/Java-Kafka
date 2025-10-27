-- Flyway migration for admin database
USE
admin_db;

-- System logs table
CREATE TABLE IF NOT EXISTS system_logs
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    service_name
    VARCHAR
(
    100
) NOT NULL,
    method_name VARCHAR
(
    100
) NOT NULL,
    class_name VARCHAR
(
    200
) NOT NULL,
    log_level VARCHAR
(
    20
) NOT NULL,
    message TEXT,
    execution_time_ms BIGINT,
    user_id VARCHAR
(
    100
),
    session_id VARCHAR
(
    100
),
    request_id VARCHAR
(
    100
),
    ip_address VARCHAR
(
    45
),
    user_agent TEXT,
    exception_stack TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_service_name
(
    service_name
),
    INDEX idx_created_at
(
    created_at
),
    INDEX idx_log_level
(
    log_level
)
    );

-- Sample data
INSERT INTO system_logs (service_name, method_name, class_name, log_level, message, execution_time_ms, created_at)
VALUES ('admin-service', 'getLogs', 'LogController', 'INFO', 'Retrieved logs successfully', 150, NOW()),
       ('admin-service', 'getStatistics', 'LogController', 'INFO', 'Statistics retrieved successfully', 200, NOW());
