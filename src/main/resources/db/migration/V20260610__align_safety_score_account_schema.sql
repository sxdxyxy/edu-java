-- ============================================================
-- V20260610__align_safety_score_account_schema.sql
-- 修 D1-A 暴露的 schema 漂移:
--   库内 t_safety_score_account 只有 8 列,但 SafetyScoreAccount 实体期望 21 列,
--   V20260526_3 因 flyway baseline=20260606 从未执行
-- 措施: ALTER TABLE 补齐缺失列(列名按 V20260526_3 原 DDL + 实体 createBy/updateBy 模式)
-- ============================================================
--
-- 2026-06-14 修订: 改为幂等写法(INFORMATION_SCHEMA 判列/索引存在性).
--   原因: dev-sts 上这张表已被手动/V20260618 提前补齐所有列 + 加了索引,
--   直接 ALTER 会报 "Duplicate column name",启动卡 Flyway 失败.
--   切到幂等后, 不管列是否已存在都跑得通.
--
-- 实现方式: 用 PREPARE/EXECUTE 动态 SQL (每列单条 statement), 配合
--   INFORMATION_SCHEMA 判存在性. 不用 stored procedure (Flyway 9 默认
--   MySQL parser 不支持 DELIMITER, 写在 SP 里需要 ; 来结尾, 会让 Flyway
--   切错语句边界).
-- ============================================================

-- 缺失列: user_id, project_id, work_type, person_work_type(已存在,跳过),
--          initial_score, annual_reset_date, status, created_at, updated_at
--          create_by, update_by, is_delete, delete_by, delete_time, delete_reason, remark

-- 1) 必补的列(幂等: 仅在列不存在时 ADD)

-- user_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN user_id BIGINT NULL COMMENT ''用户ID(关联sys_user)'' AFTER person_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- project_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN project_id BIGINT NULL COMMENT ''项目部ID'' AFTER user_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'project_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- work_type
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN work_type VARCHAR(50) NOT NULL DEFAULT ''worker'' COMMENT ''岗位类型 worker/specialized/safety_admin'' AFTER project_id', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'work_type');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- initial_score
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN initial_score INT NOT NULL DEFAULT 12 COMMENT ''初始积分(12/15/10)'' AFTER work_type', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'initial_score');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- annual_reset_date
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN annual_reset_date DATE NULL COMMENT ''年度清零日期'' AFTER current_score', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'annual_reset_date');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- status
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN status VARCHAR(20) DEFAULT ''active'' COMMENT ''状态 active/frozen'' AFTER annual_reset_date', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'status');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- created_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER status', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- updated_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'updated_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN update_time DATETIME NULL COMMENT ''更新时间(MyBatis-Plus 字段)'' AFTER update_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'update_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1'' AFTER update_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人'' AFTER is_delete', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间'' AFTER delete_by', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因'' AFTER delete_time', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注'' AFTER delete_reason', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) 加索引(幂等: 仅在索引不存在时 ADD)

-- idx_user_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_user_id (user_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_project_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_project_id (project_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_project_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_status
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_status (status)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_status');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 历史 person_work_type 已有,改名注释? 不动 schema
--    safety_code 字段是较新业务字段,保留

-- 4) 老 person_id 唯一键已经存在 (uk_person_id),保留
