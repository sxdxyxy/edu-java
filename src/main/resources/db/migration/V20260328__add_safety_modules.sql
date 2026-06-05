-- 智慧安全准入机制 - 数据库迁移脚本
-- 执行时间：2026-03-28
-- 描述：创建安全码、资质证件、违章记录、准入记录、实操评估等表

-- 1. 安全码表
CREATE TABLE IF NOT EXISTS `safety_codes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
    `code` VARCHAR(64) NOT NULL COMMENT '安全码值（加密）',
    `color` VARCHAR(8) NOT NULL DEFAULT 'green' COMMENT '颜色标识：green/yellow/red',
    `status` VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '状态：active/suspended/expired',
    `valid_from` DATETIME COMMENT '有效期开始',
    `valid_to` DATETIME COMMENT '有效期结束',
    `qr_code_data` JSON COMMENT '二维码数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_color` (`color`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全码表';

-- 2. 资质证件表
CREATE TABLE IF NOT EXISTS `qualifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
    `cert_type` VARCHAR(64) NOT NULL COMMENT '证件类型：电工证/焊工证等',
    `cert_no` VARCHAR(128) NOT NULL COMMENT '证件编号',
    `issue_date` DATE COMMENT '发证日期',
    `expiry_date` DATE COMMENT '到期日期',
    `issuing_authority` VARCHAR(256) COMMENT '发证机关',
    `cert_photo_url` VARCHAR(512) COMMENT '证件照片 URL',
    `status` VARCHAR(32) NOT NULL DEFAULT 'valid' COMMENT '状态：valid/expiring/expired',
    `verified` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否核验：0-未核验，1-已核验',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_cert_type` (`cert_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资质证件表';

-- 3. 违章记录表
CREATE TABLE IF NOT EXISTS `violation_records` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
    `violation_type` VARCHAR(64) NOT NULL COMMENT '违章类型',
    `violation_desc` VARCHAR(512) COMMENT '违章描述',
    `score` INT NOT NULL DEFAULT 0 COMMENT '扣分数值',
    `violation_date` DATETIME NOT NULL COMMENT '违章日期',
    `handler` VARCHAR(64) COMMENT '处理人',
    `status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/handled/cancelled',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_violation_date` (`violation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违章记录表';

-- 4. 准入记录表
CREATE TABLE IF NOT EXISTS `access_records` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
    `access_type` VARCHAR(64) NOT NULL COMMENT '准入类型：entry/exit',
    `location` VARCHAR(256) COMMENT '准入地点',
    `safety_code_color` VARCHAR(8) COMMENT '准入时安全码颜色',
    `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '准入时间',
    `result` VARCHAR(32) NOT NULL DEFAULT 'allowed' COMMENT '结果：allowed/denied',
    `reason` VARCHAR(512) COMMENT '拒绝原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='准入记录表';

-- 5. 实操评估表
CREATE TABLE IF NOT EXISTS `practical_assessments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
    `assessment_type` VARCHAR(64) NOT NULL COMMENT '评估类型',
    `assessment_content` TEXT COMMENT '评估内容',
    `score` DECIMAL(5,2) COMMENT '得分',
    `result` VARCHAR(32) NOT NULL COMMENT '结果：pass/fail',
    `assessor` VARCHAR(64) COMMENT '评估人',
    `assessment_date` DATETIME NOT NULL COMMENT '评估日期',
    `remark` VARCHAR(512) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_assessment_date` (`assessment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实操评估表';

-- 6. 修改 users 表，添加安全相关字段
ALTER TABLE `users` 
ADD COLUMN `safety_code_color` VARCHAR(8) DEFAULT 'green' COMMENT '安全码颜色：green/yellow/red' AFTER `email`,
ADD COLUMN `safety_score` INT DEFAULT 100 COMMENT '安全分数' AFTER `safety_code_color`,
ADD COLUMN `total_violation_score` INT DEFAULT 0 COMMENT '累计违章扣分' AFTER `safety_score`,
ADD COLUMN `last_violation_date` DATETIME COMMENT '最近违章日期' AFTER `total_violation_score`,
ADD COLUMN `safety_training_passed` TINYINT(1) DEFAULT 0 COMMENT '安全培训是否合格：0-未合格，1-合格' AFTER `last_violation_date`;
