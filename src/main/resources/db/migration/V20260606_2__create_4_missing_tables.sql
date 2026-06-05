-- 补充 4 张缺失表 V20260606
-- person_archives_config  /  person_archives_data  /  project_person_roles  /  sys_org_role

-- 1. 人员档案配置 (一人一档 → 档案配置管理)
CREATE TABLE IF NOT EXISTS `person_archives_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `work_type` INT DEFAULT NULL COMMENT '工种类型 (1=普工，2=电工等)',
  `config_name` VARCHAR(200) DEFAULT NULL COMMENT '档案配置名称',
  `config_code` VARCHAR(64) DEFAULT NULL COMMENT '配置编码',
  `org_id` BIGINT DEFAULT NULL COMMENT '所属单位/机构 ID',
  `project_id` BIGINT DEFAULT NULL COMMENT '所属项目 ID',
  `engineering_id` BIGINT DEFAULT NULL COMMENT '关联工程项目 ID',
  `template_fields` TEXT COMMENT '模板字段配置 (JSON)',
  `document_template_url` VARCHAR(500) DEFAULT NULL COMMENT '文档模板 URL',
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `is_delete` INT NOT NULL DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(500) DEFAULT NULL,
  `remark` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_work_type` (`work_type`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_engineering_id` (`engineering_id`),
  KEY `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员档案配置';

-- 2. 人员档案数据 (一人一档 → 档案数据管理)
CREATE TABLE IF NOT EXISTS `person_archives_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `config_id` BIGINT DEFAULT NULL COMMENT '关联配置 ID',
  `person_id` BIGINT DEFAULT NULL COMMENT '人员 ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `work_type` INT DEFAULT NULL COMMENT '工种类型',
  `org_id` BIGINT DEFAULT NULL COMMENT '机构 ID',
  `project_id` BIGINT DEFAULT NULL COMMENT '项目 ID',
  `team_name` VARCHAR(100) DEFAULT NULL COMMENT '班组名称',
  `data_content` TEXT COMMENT '档案数据内容 (JSON)',
  `attachments` TEXT COMMENT '附件 URL 列表 (JSON)',
  `archived_at` DATETIME DEFAULT NULL COMMENT '归档时间',
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `is_delete` INT NOT NULL DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(500) DEFAULT NULL,
  `remark` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员档案数据';

-- 3. 项目人员职务 (普通项目的人员角色)
CREATE TABLE IF NOT EXISTS `project_person_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `person_id` BIGINT DEFAULT NULL COMMENT '人员 ID',
  `project_id` BIGINT DEFAULT NULL COMMENT '项目 ID',
  `role_code` VARCHAR(64) DEFAULT NULL COMMENT '职务编码 (PROJECT_MANAGER/SAFETY_SUPERVISOR/SAFETY_OFFICER/TEAM_LEADER/WORKER)',
  `role_name` VARCHAR(64) DEFAULT NULL COMMENT '职务名称',
  `start_date` DATE DEFAULT NULL COMMENT '任职开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '任职结束日期',
  `is_current` TINYINT NOT NULL DEFAULT 1 COMMENT '是否当前任职: 0=历史 1=当前',
  `create_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `is_delete` INT NOT NULL DEFAULT 0,
  `delete_by` BIGINT DEFAULT NULL,
  `delete_time` DATETIME DEFAULT NULL,
  `delete_reason` VARCHAR(500) DEFAULT NULL,
  `remark` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_is_current` (`is_current`),
  KEY `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目人员职务';

-- 4. 项目部内部角色 (组织级角色)
CREATE TABLE IF NOT EXISTS `sys_org_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id` BIGINT DEFAULT NULL COMMENT '项目部ID',
  `role_name` VARCHAR(64) DEFAULT NULL COMMENT '角色名称',
  `role_code` VARCHAR(64) DEFAULT NULL COMMENT '角色编码',
  `role_type` VARCHAR(32) DEFAULT NULL COMMENT '角色类型',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目部内部角色';

-- =====================================================
-- 补充字段(实体里有但表没建)
-- =====================================================

-- person_archives_data 缺:archive_data, generated_doc_url, archive_number, status, created_at, updated_at
-- (用 information_schema 守卫,可重复执行)
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='archive_data')=0,
  'ALTER TABLE person_archives_data ADD COLUMN archive_data TEXT COMMENT "档案数据 (JSON)" AFTER project_id',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='generated_doc_url')=0,
  'ALTER TABLE person_archives_data ADD COLUMN generated_doc_url VARCHAR(500) DEFAULT NULL AFTER archive_data',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='archive_number')=0,
  'ALTER TABLE person_archives_data ADD COLUMN archive_number VARCHAR(64) DEFAULT NULL AFTER generated_doc_url',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='status')=0,
  'ALTER TABLE person_archives_data ADD COLUMN status VARCHAR(32) DEFAULT NULL AFTER archive_number',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='created_at')=0,
  'ALTER TABLE person_archives_data ADD COLUMN created_at DATETIME DEFAULT NULL AFTER status',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_data' AND column_name='updated_at')=0,
  'ALTER TABLE person_archives_data ADD COLUMN updated_at DATETIME DEFAULT NULL',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

-- person_archives_config 缺:document_template_name, sort_order, is_default, is_active, archive_items
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_config' AND column_name='document_template_name')=0,
  'ALTER TABLE person_archives_config ADD COLUMN document_template_name VARCHAR(200) DEFAULT NULL AFTER document_template_url',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_config' AND column_name='sort_order')=0,
  'ALTER TABLE person_archives_config ADD COLUMN sort_order INT DEFAULT 0 AFTER document_template_name',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_config' AND column_name='is_default')=0,
  'ALTER TABLE person_archives_config ADD COLUMN is_default TINYINT DEFAULT 0 AFTER sort_order',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_config' AND column_name='is_active')=0,
  'ALTER TABLE person_archives_config ADD COLUMN is_active TINYINT DEFAULT 1 AFTER is_default',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='person_archives_config' AND column_name='archive_items')=0,
  'ALTER TABLE person_archives_config ADD COLUMN archive_items TEXT AFTER is_active',
  'DO 0')); PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
