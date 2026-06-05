-- 安全监管与培训系统联动 - 课程准入规则表
-- 执行时间：2026-05-26
-- 描述：创建课程准入规则表，定义培训课程的报名准入条件

CREATE TABLE IF NOT EXISTS `t_course_admission_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID（关联t_train_course）',
    `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型 safety_score/special_cert/training_completed',
    `rule_condition` VARCHAR(100) COMMENT '规则条件（如：GREEN/YELLOW/RED 或 cert_valid）',
    `rule_value` VARCHAR(200) COMMENT '规则值',
    `is_mandatory` TINYINT DEFAULT 1 COMMENT '是否强制检查 0=提示 1=禁止',
    `error_message` VARCHAR(500) COMMENT '不通过时的提示信息',
    `sort_order` INT DEFAULT 0 COMMENT '优先级',
    `status` VARCHAR(20) DEFAULT 'enabled' COMMENT '状态 enabled/disabled',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_rule_type` (`rule_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程准入规则表';
