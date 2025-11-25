-- Flyway migration for kafka database
USE
kafka_db;

-- Kafka topics table
CREATE TABLE IF NOT EXISTS kafka_topics
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    topic_name
    VARCHAR
(
    100
) NOT NULL UNIQUE,
    partitions INT NOT NULL DEFAULT 1,
    replication_factor SMALLINT NOT NULL DEFAULT 1,
    is_internal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_topic_name
(
    topic_name
),
    INDEX idx_is_internal
(
    is_internal
)
    );

-- Kafka consumer groups table
CREATE TABLE IF NOT EXISTS kafka_consumer_groups
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    group_id
    VARCHAR
(
    100
) NOT NULL UNIQUE,
    state VARCHAR
(
    20
) NOT NULL,
    members_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_group_id
(
    group_id
),
    INDEX idx_state
(
    state
)
    );

-- System logs table (for LoggingAspect)
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

-- Sample topics
INSERT INTO kafka_topics (topic_name, partitions, replication_factor, is_internal)
VALUES ('service-calls', 3, 1, FALSE),
       ('schedule-tasks', 3, 1, FALSE),
       ('data-processing', 3, 1, FALSE),
       ('notifications', 3, 1, FALSE),
       ('workflow-events', 3, 1, FALSE);
