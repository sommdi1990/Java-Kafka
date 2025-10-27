-- Create databases for all services
CREATE
DATABASE IF NOT EXISTS admin_db;
CREATE
DATABASE IF NOT EXISTS cbi_db;
CREATE
DATABASE IF NOT EXISTS schedule_db;
CREATE
DATABASE IF NOT EXISTS workflow_db;
CREATE
DATABASE IF NOT EXISTS gateway_db;
CREATE
DATABASE IF NOT EXISTS kafka_db;

-- Create users for each service
CREATE
USER IF NOT EXISTS 'admin_user'@'%' IDENTIFIED BY 'admin_password';
CREATE
USER IF NOT EXISTS 'cbi_user'@'%' IDENTIFIED BY 'cbi_password';
CREATE
USER IF NOT EXISTS 'schedule_user'@'%' IDENTIFIED BY 'schedule_password';
CREATE
USER IF NOT EXISTS 'workflow_user'@'%' IDENTIFIED BY 'workflow_password';
CREATE
USER IF NOT EXISTS 'gateway_user'@'%' IDENTIFIED BY 'gateway_password';
CREATE
USER IF NOT EXISTS 'kafka_user'@'%' IDENTIFIED BY 'kafka_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON admin_db.* TO
'admin_user'@'%';
GRANT ALL PRIVILEGES ON cbi_db.* TO
'cbi_user'@'%';
GRANT ALL PRIVILEGES ON schedule_db.* TO
'schedule_user'@'%';
GRANT ALL PRIVILEGES ON workflow_db.* TO
'workflow_user'@'%';
GRANT ALL PRIVILEGES ON gateway_db.* TO
'gateway_user'@'%';
GRANT ALL PRIVILEGES ON kafka_db.* TO
'kafka_user'@'%';

-- Flush privileges
FLUSH
PRIVILEGES;
