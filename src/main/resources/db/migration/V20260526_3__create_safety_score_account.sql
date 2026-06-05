-- 安全监管与培训系统联动 - 安全积分账户表
-- 执行时间：2026-05-26
-- 描述：创建安全积分账户表，记录人员的安全积分余额

CREATE TABLE IF NOT EXISTS `t_safety_score_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `person_id` BIGINT NOT NULL COMMENT '人员ID',
    `user_id` BIGINT COMMENT '用户ID（关联sys_user）',
    `project_id` BIGINT COMMENT '项目部ID（关联t_project）',
    `work_type` VARCHAR(50) NOT NULL DEFAULT 'worker' COMMENT '岗位类型 worker/specialized/safety_admin',
    `initial_score` INT NOT NULL COMMENT '初始积分（12/15/10）',
    `current_score` INT NOT NULL COMMENT '当前积分',
    `annual_reset_date` DATE COMMENT '年度清零日期',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态 active/frozen',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_person_id` (`person_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全积分账户表';
