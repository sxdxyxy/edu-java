-- 安全培训考试系统测试数据库初始化脚本

-- 简化版表结构（用于集成测试）
CREATE TABLE IF NOT EXISTS t_safety_score_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    person_id BIGINT,
    user_id BIGINT,
    project_id BIGINT,
    work_type VARCHAR(50),
    person_work_type INT,
    initial_score INT DEFAULT 12,
    current_score INT DEFAULT 12,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_violation_type_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    violation_code VARCHAR(50),
    violation_name VARCHAR(100),
    violation_level VARCHAR(20),
    deduct_score INT DEFAULT 0,
    trigger_training BOOLEAN DEFAULT FALSE,
    training_hours INT DEFAULT 0,
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'enabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_special_work_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_type_code VARCHAR(50),
    work_type_name VARCHAR(100),
    danger_level VARCHAR(20),
    default_score INT DEFAULT 12,
    certificate_valid_years INT DEFAULT 3,
    status VARCHAR(20) DEFAULT 'enabled',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO t_violation_type_config (violation_code, violation_name, violation_level, deduct_score, status)
SELECT 'NO_HELMET', '未佩戴安全帽', 'minor', 2, 'enabled'
WHERE NOT EXISTS (SELECT 1 FROM t_violation_type_config WHERE violation_code = 'NO_HELMET');

INSERT INTO t_special_work_type (work_type_code, work_type_name, danger_level, default_score)
SELECT 'ELEVATOR', '电梯作业', 'high', 15
WHERE NOT EXISTS (SELECT 1 FROM t_special_work_type WHERE work_type_code = 'ELEVATOR');
