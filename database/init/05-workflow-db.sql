-- Flyway migration for workflow database
USE
workflow_db;

-- Workflow definitions table
CREATE TABLE IF NOT EXISTS workflow_definitions
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
) NOT NULL,
    description TEXT,
    version VARCHAR
(
    20
) NOT NULL,
    definition_json TEXT,
    status ENUM
(
    'DRAFT',
    'ACTIVE',
    'INACTIVE',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',
    created_by VARCHAR
(
    100
),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status
(
    status
),
    INDEX idx_created_by
(
    created_by
)
    );

-- Workflow instances table
CREATE TABLE IF NOT EXISTS workflow_instances
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    workflow_definition_id
    BIGINT
    NOT
    NULL,
    instance_name
    VARCHAR
(
    100
) NOT NULL,
    status ENUM
(
    'RUNNING',
    'COMPLETED',
    'FAILED',
    'PAUSED',
    'CANCELLED'
) NOT NULL DEFAULT 'RUNNING',
    current_step VARCHAR
(
    100
),
    context_data TEXT,
    started_by VARCHAR
(
    100
),
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    execution_time_ms BIGINT,
    error_message TEXT,
    FOREIGN KEY
(
    workflow_definition_id
) REFERENCES workflow_definitions
(
    id
),
    INDEX idx_workflow_definition_id
(
    workflow_definition_id
),
    INDEX idx_status
(
    status
),
    INDEX idx_started_at
(
    started_at
)
    );

-- Sample workflow definitions
INSERT INTO workflow_definitions (name, description, version, definition_json, status, created_by)
VALUES ('Sample Workflow', 'A sample workflow for demonstration', '1.0',
        '{"name":"Sample Workflow","version":"1.0","steps":[{"name":"step1","type":"service_call","service":"cbi-service","endpoint":"/api/cbi/rest/1"},{"name":"step2","type":"schedule_task","task":"data-processing","cron":"0 0 12 * * ?"},{"name":"step3","type":"notification","notificationType":"email","message":"Workflow completed successfully"}]}',
        'ACTIVE', 'system');
