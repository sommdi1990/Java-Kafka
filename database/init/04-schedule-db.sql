-- Flyway migration for schedule database
USE
schedule_db;

-- Scheduled tasks table
CREATE TABLE IF NOT EXISTS scheduled_tasks
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    task_name
    VARCHAR
(
    100
) NOT NULL UNIQUE,
    description TEXT,
    cron_expression VARCHAR
(
    100
) NOT NULL,
    task_type ENUM
(
    'DATA_PROCESSING',
    'REPORT_GENERATION',
    'DATA_CLEANUP',
    'NOTIFICATION',
    'INTEGRATION'
) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_execution TIMESTAMP NULL,
    next_execution TIMESTAMP NULL,
    execution_count BIGINT DEFAULT 0,
    success_count BIGINT DEFAULT 0,
    failure_count BIGINT DEFAULT 0,
    average_execution_time_ms BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_task_type
(
    task_type
),
    INDEX idx_is_active
(
    is_active
),
    INDEX idx_next_execution
(
    next_execution
)
    );

-- Sample scheduled tasks
INSERT INTO scheduled_tasks (task_name, description, cron_expression, task_type, is_active)
VALUES ('Daily Data Processing', 'Process daily data batch', '0 0 2 * * ?', 'DATA_PROCESSING', TRUE),
       ('Weekly Report Generation', 'Generate weekly reports', '0 0 9 ? * MON', 'REPORT_GENERATION', TRUE),
       ('Hourly Cleanup', 'Clean up temporary files', '0 0 * * * ?', 'DATA_CLEANUP', TRUE);
