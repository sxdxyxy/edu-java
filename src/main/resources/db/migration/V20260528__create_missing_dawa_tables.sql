-- V20260528: 创建缺失的 dawa 安全监管模块表和修复列
-- 在 safe_train_backup.sql 恢复后，发现 5 张表和部分列被遗漏

-- 1. 违章记录表
CREATE TABLE IF NOT EXISTS `violation_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `project_id` BIGINT DEFAULT NULL,
  `org_id` BIGINT DEFAULT NULL,
  `violation_type` VARCHAR(100) DEFAULT NULL,
  `severity` VARCHAR(20) DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `evidence_photos` TEXT DEFAULT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `handler_id` BIGINT DEFAULT NULL,
  `processed_at` DATETIME DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` INT DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(255) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违章记录';

-- 2. 准入记录表
CREATE TABLE IF NOT EXISTS `access_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `project_id` BIGINT DEFAULT NULL,
  `org_id` BIGINT DEFAULT NULL,
  `access_time` DATETIME DEFAULT NULL,
  `exit_time` DATETIME DEFAULT NULL,
  `access_type` VARCHAR(50) DEFAULT NULL,
  `safety_code_color` VARCHAR(20) DEFAULT NULL,
  `gate_id` VARCHAR(100) DEFAULT NULL,
  `camera_snapshot` VARCHAR(500) DEFAULT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `remarks` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `is_delete` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='准入记录';

-- 3. 实操考核表
CREATE TABLE IF NOT EXISTS `practical_assessments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `project_id` BIGINT DEFAULT NULL,
  `org_id` BIGINT DEFAULT NULL,
  `assessment_type` VARCHAR(50) DEFAULT NULL,
  `assessment_date` DATETIME DEFAULT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `examiner_id` BIGINT DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `result` VARCHAR(20) DEFAULT NULL,
  `video_url` VARCHAR(500) DEFAULT NULL,
  `evidence_photos` TEXT DEFAULT NULL,
  `remarks` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `is_delete` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实操考核';

-- 4. 资质证件表
CREATE TABLE IF NOT EXISTS `qualifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `project_id` BIGINT DEFAULT NULL,
  `cert_type` VARCHAR(100) DEFAULT NULL,
  `cert_no` VARCHAR(100) DEFAULT NULL,
  `issue_date` DATE DEFAULT NULL,
  `expiry_date` DATE DEFAULT NULL,
  `issuing_authority` VARCHAR(200) DEFAULT NULL,
  `cert_photo_url` VARCHAR(500) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'valid',
  `verified` TINYINT(1) DEFAULT 0,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `holder_name` VARCHAR(100) DEFAULT NULL,
  `org_id` BIGINT DEFAULT NULL,
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` TINYINT(1) DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(255) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资质证件';

-- 5. 安全码表
CREATE TABLE IF NOT EXISTS `safety_codes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `person_id` BIGINT DEFAULT NULL,
  `project_id` BIGINT DEFAULT NULL,
  `org_id` BIGINT DEFAULT NULL,
  `code` VARCHAR(64) DEFAULT NULL,
  `color` VARCHAR(20) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'active',
  `valid_from` DATETIME DEFAULT NULL,
  `valid_to` DATETIME DEFAULT NULL,
  `qr_code_data` TEXT DEFAULT NULL,
  `remarks` VARCHAR(500) DEFAULT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` INT DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(255) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_color` (`color`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全码';

-- 6. 给 sys_org 补充缺失的工程关联字段
ALTER TABLE sys_org ADD COLUMN IF NOT EXISTS `short_name` VARCHAR(100) DEFAULT NULL COMMENT '机构简称' AFTER `name`;
ALTER TABLE sys_org ADD COLUMN IF NOT EXISTS `engineering_id` BIGINT DEFAULT NULL COMMENT '关联工程项目ID' AFTER `short_name`;
