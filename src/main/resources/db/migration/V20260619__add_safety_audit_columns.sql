-- =====================================================
-- V20260619: 补齐 safety 模块余下审计列
-- 说明: V20260612_2 在 dev-sts 上 @has_old=0 全部 no-op,
--   V20260618 只补了 t_safety_score_account / t_safety_appeal_record
--   仍剩 5 张 safety 表缺 BaseEntity 审计列, 导致 MyBatis-Plus
--   `SELECT *` 报 "Unknown column 'create_time' in 'field list'"
-- 措施: INFORMATION_SCHEMA + PREPARE/EXECUTE 幂等补列
-- 注: MySQL 8.0.45 不支持 `ADD COLUMN IF NOT EXISTS`, 必须用
--     INFORMATION_SCHEMA 判存在性后 PREPARE 动态执行
-- =====================================================

-- ============================================================
-- 1) t_violation_type_config: 缺 create_time, update_time
-- ============================================================

-- create_time (BaseEntity 字段)
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_violation_type_config ADD COLUMN create_time DATETIME NULL COMMENT ''创建时间(MyBatis-Plus)'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_violation_type_config' AND COLUMN_NAME = 'create_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_violation_type_config ADD COLUMN update_time DATETIME NULL COMMENT ''更新时间(MyBatis-Plus)'' AFTER create_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_violation_type_config' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2) t_safety_appeal_record: 缺 delete_by, delete_time,
--   delete_reason, remark (V20260618 已补 create_by/update_by/
--   is_delete 和 rename update_time->updated_at, 但没补删除相关)
-- ============================================================

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3) t_safety_config: 缺全部 9 个 BaseEntity 审计列
--    (除 created_at/updated_at 外全无)
-- ============================================================

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN create_time DATETIME NULL COMMENT ''创建时间(MyBatis-Plus)'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'create_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN update_time DATETIME NULL COMMENT ''更新时间(MyBatis-Plus)'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_config ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_config' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 4) t_safety_notification: 缺 8 个 (只有 create_time, 无 update_time)
-- ============================================================

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER create_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN update_time DATETIME NULL COMMENT ''更新时间(MyBatis-Plus)'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_notification ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_notification' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 5) t_special_work_type: 缺全部 9 个 BaseEntity 审计列
--    (除 created_at/updated_at 外全无)
-- ============================================================

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN create_time DATETIME NULL COMMENT ''创建时间(MyBatis-Plus)'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'create_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN update_time DATETIME NULL COMMENT ''更新时间(MyBatis-Plus)'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_special_work_type ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_special_work_type' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 6) t_safety_score_transaction: 缺 create_by, create_time
--    (其他 7 个已存在)
-- ============================================================

-- create_by (现有: remarks, created_at, update_by, update_time, is_delete, ...)
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN create_time DATETIME NULL COMMENT ''创建时间(MyBatis-Plus)'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'create_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
