-- 安全监管与培训系统联动 - 安全积分变动流水表
-- 执行时间：2026-05-26
-- 描述：创建安全积分变动流水表，记录所有积分变动历史

CREATE TABLE IF NOT EXISTS `t_safety_score_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `person_id` BIGINT NOT NULL COMMENT '人员ID',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型 deduct/restore/reset/adjust',
    `change_amount` INT NOT NULL COMMENT '变动分值 正数为恢复 负数为扣减',
    `before_score` INT NOT NULL COMMENT '变动前积分',
    `after_score` INT NOT NULL COMMENT '变动后积分',
    `trigger_reason` VARCHAR(200) COMMENT '触发原因',
    `violation_record_id` BIGINT COMMENT '关联违章记录ID',
    `retraining_record_id` BIGINT COMMENT '关联再培训记录ID',
    `operator_id` BIGINT COMMENT '操作人ID',
    `remarks` VARCHAR(500) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (`id`),
    KEY `idx_person_id` (`person_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全积分变动流水表';
