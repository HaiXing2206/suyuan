-- 模拟数据脚本（MySQL 8.0+）
-- 目标：一次性补充 >=100 条测试数据，覆盖台账、任务、审核、报告与审计日志。
-- 建议在测试环境执行。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- 1) 生成 120 条数据要素台账（data_element_ledgers）
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
)
INSERT INTO data_element_ledgers (
    element_id,
    element_name,
    source,
    owner_name,
    department,
    purpose,
    classification_level,
    data_level,
    sensitive_flag,
    archive_status,
    metadata_definition,
    quality_note,
    created_at,
    updated_at,
    attachment_urls,
    business_tags,
    risk_tags,
    quality_tags,
    lineage_info
)
SELECT
    CONCAT('ELM-', LPAD(n, 4, '0')),
    CONCAT('模拟数据要素-', LPAD(n, 3, '0')),
    ELT(1 + (n % 4), 'ERP', 'CRM', 'IoT', '第三方平台'),
    CONCAT('owner_', LPAD(1 + (n % 20), 2, '0')),
    ELT(1 + (n % 6), '财务部', '风控部', '运营部', '市场部', '技术部', '法务部'),
    ELT(1 + (n % 5), '监管报送', '经营分析', '风险识别', '客户服务', '流程优化'),
    ELT(1 + (n % 4), '公开', '内部', '敏感', '机密'),
    ELT(1 + (n % 4), 'L1', 'L2', 'L3', 'L4'),
    IF(n % 5 = 0, 1, 0),
    ELT(1 + (n % 3), 'ACTIVE', 'PENDING_ARCHIVE', 'ARCHIVED'),
    CONCAT('{"fields":["f', n, '","f', n + 1, '"],"ver":"v', 1 + (n % 3), '"}'),
    ELT(1 + (n % 4), '完整性良好', '存在少量缺失', '需补充口径说明', '建议增加校验规则'),
    DATE_SUB(NOW(), INTERVAL (120 - n) DAY),
    DATE_SUB(NOW(), INTERVAL (120 - n) DAY),
    CONCAT('https://mock.local/attach/ELM-', LPAD(n, 4, '0')),
    ELT(1 + (n % 4), '核心业务', '增长分析', '运营监控', '合规治理'),
    ELT(1 + (n % 4), '低风险', '中风险', '高风险', '需复核'),
    ELT(1 + (n % 4), 'A', 'B', 'C', 'D'),
    CONCAT('来源系统->ODS->DWD->APP(', n, ')')
FROM seq
ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    quality_note = VALUES(quality_note),
    archive_status = VALUES(archive_status);

-- 2) 生成 120 条评估任务（evaluation_tasks）
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
)
INSERT INTO evaluation_tasks (
    task_id,
    task_name,
    element_id,
    indicator_version,
    owner,
    due_time,
    status,
    result_score,
    result_grade,
    issue_list,
    data_level,
    sensitive_flag,
    archive_status,
    created_at,
    updated_at
)
SELECT
    CONCAT('TSK-', LPAD(n, 4, '0')),
    CONCAT('模拟评估任务-', LPAD(n, 3, '0')),
    CONCAT('ELM-', LPAD(n, 4, '0')),
    CONCAT('v', 1 + (n % 3), '.0'),
    CONCAT('task_owner_', LPAD(1 + (n % 15), 2, '0')),
    DATE_ADD(NOW(), INTERVAL (n % 45) DAY),
    ELT(1 + (n % 6), 'DRAFT', 'IN_PROGRESS', 'PENDING_INITIAL_REVIEW', 'PENDING_REVIEW', 'PENDING_FINAL', 'APPROVED'),
    ROUND(60 + (n % 41) + (n % 7) * 0.1, 2),
    ELT(1 + (n % 4), 'A', 'B', 'C', 'D'),
    CONCAT('问题', n, ': 字段口径校验/完整性校验'),
    ELT(1 + (n % 4), 'L1', 'L2', 'L3', 'L4'),
    IF(n % 6 = 0, 1, 0),
    ELT(1 + (n % 3), 'ACTIVE', 'PENDING_ARCHIVE', 'ARCHIVED'),
    DATE_SUB(NOW(), INTERVAL (120 - n) DAY),
    NOW()
FROM seq
ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    status = VALUES(status),
    result_score = VALUES(result_score),
    result_grade = VALUES(result_grade);

-- 3) 为每个任务生成 3 条审批流记录，共 360 条（approval_flows）
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
)
INSERT INTO approval_flows (
    task_id,
    approval_stage,
    approver_role,
    approver_name,
    status,
    comment,
    action_time
)
SELECT CONCAT('TSK-', LPAD(n, 4, '0')), 'INITIAL', 'BUSINESS_REVIEWER', CONCAT('reviewer_biz_', LPAD(1 + (n % 12), 2, '0')),
       ELT(1 + (n % 3), 'APPROVED', 'APPROVED', 'REJECTED'),
       CONCAT('初审意见-', n),
       DATE_SUB(NOW(), INTERVAL (130 - n) DAY)
FROM seq
UNION ALL
SELECT CONCAT('TSK-', LPAD(n, 4, '0')), 'REVIEW', 'RISK_REVIEWER', CONCAT('reviewer_risk_', LPAD(1 + (n % 10), 2, '0')),
       ELT(1 + (n % 4), 'APPROVED', 'APPROVED', 'REJECTED', 'APPROVED'),
       CONCAT('复审意见-', n),
       DATE_SUB(NOW(), INTERVAL (125 - n) DAY)
FROM seq
UNION ALL
SELECT CONCAT('TSK-', LPAD(n, 4, '0')), 'FINAL', 'MANAGER', CONCAT('manager_', LPAD(1 + (n % 8), 2, '0')),
       ELT(1 + (n % 5), 'APPROVED', 'APPROVED', 'APPROVED', 'REJECTED', 'APPROVED'),
       CONCAT('终审意见-', n),
       DATE_SUB(NOW(), INTERVAL (120 - n) DAY)
FROM seq;

-- 4) 生成 120 条评估报告（evaluation_reports）
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
)
INSERT INTO evaluation_reports (
    task_id,
    report_name,
    template_name,
    report_version,
    preview_url,
    export_format,
    export_status,
    archive_status,
    created_at
)
SELECT
    CONCAT('TSK-', LPAD(n, 4, '0')),
    CONCAT('评估报告-', LPAD(n, 3, '0')),
    ELT(1 + (n % 2), 'STANDARD', 'DEPARTMENT'),
    CONCAT('v', 1 + (n % 4), '.', n % 10),
    CONCAT('/mock-preview/TSK-', LPAD(n, 4, '0'), '.html'),
    ELT(1 + (n % 3), 'PDF', 'WORD', 'PDF'),
    ELT(1 + (n % 3), 'GENERATED', 'EXPORTED', 'EXPORTED'),
    ELT(1 + (n % 3), 'ACTIVE', 'ARCHIVED', 'ARCHIVED'),
    DATE_SUB(NOW(), INTERVAL (120 - n) DAY)
FROM seq;

-- 5) 生成 150 条审计日志（audit_logs）
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 150
)
INSERT INTO audit_logs (
    business_type,
    business_id,
    action_type,
    operator_name,
    operator_role,
    ip_address,
    result_status,
    details,
    created_at
)
SELECT
    ELT(1 + (n % 4), 'LEDGER', 'TASK', 'APPROVAL', 'REPORT'),
    CONCAT('BIZ-', LPAD(n, 5, '0')),
    ELT(1 + (n % 5), 'CREATE', 'UPDATE', 'SUBMIT', 'APPROVE', 'ARCHIVE'),
    CONCAT('operator_', LPAD(1 + (n % 25), 2, '0')),
    ELT(1 + (n % 4), 'ANALYST', 'REVIEWER', 'MANAGER', 'AUDITOR'),
    CONCAT('10.10.', n % 20, '.', 10 + (n % 200)),
    ELT(1 + (n % 3), 'SUCCESS', 'SUCCESS', 'FAILED'),
    CONCAT('模拟审计事件-', n, '：自动生成用于联调验证'),
    DATE_SUB(NOW(), INTERVAL (150 - n) HOUR)
FROM seq;

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

-- 预估新增记录数：
-- data_element_ledgers: 120
-- evaluation_tasks: 120
-- approval_flows: 360
-- evaluation_reports: 120
-- audit_logs: 150
-- 合计：870 条
