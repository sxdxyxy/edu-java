-- 安全监管与培训系统联动 - 特种作业类型表
-- 执行时间：2026-05-26
-- 描述：创建特种作业类型配置表，支持动态配置各类特种作业

CREATE TABLE IF NOT EXISTS `t_special_work_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `work_type_code` VARCHAR(50) NOT NULL COMMENT '作业类型编码',
    `work_type_name` VARCHAR(100) NOT NULL COMMENT '作业类型名称',
    `danger_level` VARCHAR(20) DEFAULT 'medium' COMMENT '危险等级 high/medium/low',
    `default_score` INT DEFAULT 15 COMMENT '默认初始积分',
    `certificate_valid_years` INT DEFAULT 6 COMMENT '证书有效期（年）',
    `status` VARCHAR(20) DEFAULT 'enabled' COMMENT '状态 enabled/disabled',
    `remarks` VARCHAR(500) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_work_type_code` (`work_type_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特种作业类型表';
