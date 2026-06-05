-- 安全监管与培训系统联动 - 安全再培训记录表
-- 执行时间：2026-05-26
-- 描述：创建安全再培训记录表，记录违章触发后的安全再培训

CREATE TABLE IF NOT EXISTS `t_safety_retraining_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `person_id` BIGINT NOT NULL COMMENT '人员ID',
    `violation_record_id` BIGINT COMMENT '触发的违章记录ID',
    `violation_code` VARCHAR(50) COMMENT '违章代码',
    `training_course_id` BIGINT COMMENT '培训课程ID',
    `training_hours` INT DEFAULT 0 COMMENT '培训学时',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending/ongoing/completed/failed',
    `start_date` DATE COMMENT '开始日期',
    `end_date` DATE COMMENT '完成日期',
    `score_restored` INT DEFAULT 0 COMMENT '恢复的积分分值',
    `operator_id` BIGINT COMMENT '创建人ID',
    `remarks` VARCHAR(500) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_person_id` (`person_id`),
    KEY `idx_status` (`status`),
    KEY `idx_violation_record_id` (`violation_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全再培训记录表';
