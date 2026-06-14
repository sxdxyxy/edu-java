-- ============================================================
-- V20260612__align_remaining_safety_schemas.sql
-- 修 D2 系统性 schema 漂移 (5 张表 + 命名同步):
--   1) t_safety_score_transaction:  8→21 列,补 7 流水核心 + 6 audit
--   2) t_safety_retraining_record:   9→25 列,补 7 再培训业务 + 8 audit
--   3) t_course_admission_rule:    11→14 列,补 3 audit
--   4) t_safety_appeal_record:      14→17 列,补 created_at + rename update_time→updated_at + 3 audit
--   5) access_records:             13→14 列,补 org_id
-- 注: 全部 ADD COLUMN 加在表尾,不依赖中间列(避免 AFTER 引用失败)
--     mybatis-plus autoResultMap 会 SELECT 所有非 exist=false 字段
-- ============================================================
--
-- 2026-06-14 修订: 改为幂等写法(INFORMATION_SCHEMA 判列存在性).
--   原因: dev-sts 上这 5 张表已被手动/V20260618 提前补齐列, 直接 ALTER
--   会报 "Duplicate column name",启动卡 Flyway 失败.
--   切到幂等后, 不管列是否已存在都跑得通.
-- ============================================================

-- ============================================================
-- 1) t_safety_score_transaction: 补 13 列
-- ============================================================

-- change_type
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN change_type VARCHAR(32) NULL COMMENT ''变更类型 deduct/restore/reset/adjust'' AFTER account_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'change_type');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- before_score
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN before_score INT NULL COMMENT ''变更前积分'' AFTER change_amount', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'before_score');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- after_score
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN after_score INT NULL COMMENT ''变更后积分'' AFTER before_score', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'after_score');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- trigger_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN trigger_reason VARCHAR(255) NULL COMMENT ''触发原因描述'' AFTER after_score', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'trigger_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- violation_record_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN violation_record_id BIGINT NULL COMMENT ''关联违章记录'' AFTER trigger_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'violation_record_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- retraining_record_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN retraining_record_id BIGINT NULL COMMENT ''关联再培训记录'' AFTER violation_record_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'retraining_record_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remarks
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN remarks VARCHAR(500) NULL COMMENT ''备注'' AFTER retraining_record_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'remarks');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- created_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER remarks', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''MyBatis updateTime 字段'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 索引
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD KEY idx_change_type (change_type)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND INDEX_NAME = 'idx_change_type');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_transaction ADD KEY idx_violation_record_id (violation_record_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_transaction' AND INDEX_NAME = 'idx_violation_record_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2) t_safety_retraining_record: 补 15 列 + 改名 update_time→updated_at
-- ============================================================

-- violation_record_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN violation_record_id BIGINT NULL COMMENT ''关联违章记录'' AFTER person_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'violation_record_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- violation_code
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN violation_code VARCHAR(50) NULL COMMENT ''违章代码'' AFTER violation_record_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'violation_code');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- training_course_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN training_course_id BIGINT NULL COMMENT ''培训课程ID'' AFTER violation_code', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'training_course_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- training_hours
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN training_hours INT NULL COMMENT ''培训学时'' AFTER training_course_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'training_hours');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- start_date
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN start_date DATE NULL COMMENT ''开始日期'' AFTER status', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'start_date');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- end_date
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN end_date DATE NULL COMMENT ''结束日期'' AFTER start_date', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'end_date');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- operator_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN operator_id BIGINT NULL COMMENT ''操作人'' AFTER confirmed_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'operator_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remarks
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN remarks VARCHAR(500) NULL COMMENT ''备注'' AFTER operator_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'remarks');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- created_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER remarks', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''MyBatis updateTime 字段'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 改名 update_time → updated_at(实体字段 updatedAt)
SET @sql = (SELECT IF(EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'update_time') AND NOT EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'updated_at'), 'ALTER TABLE t_safety_retraining_record CHANGE COLUMN update_time updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 索引
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD KEY idx_violation_record_id (violation_record_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND INDEX_NAME = 'idx_violation_record_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_retraining_record ADD KEY idx_training_course_id (training_course_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND INDEX_NAME = 'idx_training_course_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3) t_course_admission_rule: 补 3 审计字段
-- ============================================================

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_course_admission_rule ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_course_admission_rule' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_course_admission_rule ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_course_admission_rule' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_course_admission_rule ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_course_admission_rule' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 4) t_safety_appeal_record: 补 created_at + rename update_time + 3 审计
-- ============================================================

-- created_at (注意: V8 创建时无 update_time, 用 IF + 单独列, 不依赖中间列)
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN create_by BIGINT NULL COMMENT ''创建人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN update_by BIGINT NULL COMMENT ''更新人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 改名 update_time → updated_at
SET @sql = (SELECT IF(EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'update_time') AND NOT EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'updated_at'), 'ALTER TABLE t_safety_appeal_record CHANGE COLUMN update_time updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''', 'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 5) access_records: 补 1 关联字段
-- ============================================================

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE access_records ADD COLUMN org_id BIGINT NULL COMMENT ''组织机构ID'' AFTER project_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'access_records' AND COLUMN_NAME = 'org_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 索引
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE access_records ADD KEY idx_org_id (org_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'access_records' AND INDEX_NAME = 'idx_org_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
