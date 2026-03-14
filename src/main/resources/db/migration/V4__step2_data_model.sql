-- 第二步：数据模型改造（数据库与实体）

CREATE TABLE IF NOT EXISTS data_element_ledgers (
    element_id VARCHAR(64) PRIMARY KEY,
    element_name VARCHAR(128) NOT NULL,
    source VARCHAR(128) NOT NULL,
    owner_name VARCHAR(64) NOT NULL,
    department VARCHAR(64) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    classification_level VARCHAR(32) NOT NULL,
    data_level VARCHAR(16) NOT NULL,
    sensitive_flag TINYINT(1) NOT NULL DEFAULT 0,
    archive_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    metadata_definition TEXT,
    quality_note TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluation_tasks (
    task_id VARCHAR(64) PRIMARY KEY,
    task_name VARCHAR(128) NOT NULL,
    element_id VARCHAR(64) NOT NULL,
    indicator_version VARCHAR(32) NOT NULL,
    owner VARCHAR(64) NOT NULL,
    due_time DATETIME NOT NULL,
    status VARCHAR(32) NOT NULL,
    result_score DECIMAL(10,2),
    result_grade VARCHAR(16),
    issue_list TEXT,
    data_level VARCHAR(16) NOT NULL,
    sensitive_flag TINYINT(1) NOT NULL DEFAULT 0,
    archive_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_eval_tasks_element_id (element_id),
    INDEX idx_eval_tasks_status (status)
);

CREATE TABLE IF NOT EXISTS approval_flows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL,
    approval_stage VARCHAR(16) NOT NULL,
    approver_role VARCHAR(32) NOT NULL,
    approver_name VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    action_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_approval_task_id (task_id),
    INDEX idx_approval_role_status (approver_role, status)
);

CREATE TABLE IF NOT EXISTS evaluation_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL,
    report_name VARCHAR(128) NOT NULL,
    template_name VARCHAR(64) NOT NULL,
    report_version VARCHAR(32) NOT NULL,
    preview_url VARCHAR(255),
    export_format VARCHAR(16),
    export_status VARCHAR(20) NOT NULL,
    archive_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reports_task_id (task_id),
    INDEX idx_reports_archive_status (archive_status)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type VARCHAR(32) NOT NULL,
    business_id VARCHAR(64) NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    operator_role VARCHAR(32),
    ip_address VARCHAR(64),
    result_status VARCHAR(20) NOT NULL,
    details TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_business (business_type, business_id),
    INDEX idx_audit_operator (operator_name)
);

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS data_level VARCHAR(16) NOT NULL DEFAULT 'L2',
    ADD COLUMN IF NOT EXISTS sensitive_flag TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS archive_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
