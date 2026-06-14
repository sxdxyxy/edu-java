-- =====================================================
-- V20260618: 幂等补全 safety 模块审计列 (兜底)
-- 说明: V20260610 / V20260612 在 dev-sts 上可能因 baseline 跳号 / strict mode 失败
--   此处逐列/逐索引用 INFORMATION_SCHEMA 判存在性后, 用 PREPARE/EXECUTE 动态执行
-- 注: MySQL 8.0.45 不支持 `ADD COLUMN IF NOT EXISTS`, 必须用 INFORMATION_SCHEMA 写法
--     旧版本 (5.7) 也需同样改写
-- 2026-06-14 修订: 从 `ADD COLUMN IF NOT EXISTS` 切到 INFORMATION_SCHEMA + PREPARE
-- =====================================================

-- ============================================================
-- 1) t_safety_score_account: 补可能被跳过的列 (V20260610 的 AFTER 子句顺序)
-- ============================================================

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

-- ============================================================
-- 2) 索引补齐 (V20260610 末尾)
-- ============================================================

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_user_id (user_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_project_id (project_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_project_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_score_account ADD KEY idx_status (status)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND INDEX_NAME = 'idx_status');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3) t_safety_appeal_record: 补齐列 (追加到表尾, 不依赖中间列)
-- ============================================================

-- created_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN create_by BIGINT NULL COMMENT ''创建人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN update_by BIGINT NULL COMMENT ''更新人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE t_safety_appeal_record ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_appeal_record' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 4) t_safety_appeal_record: rename update_time → updated_at
-- 实体字段是 updatedAt;如果列名仍是 update_time,统一改名为 updated_at
-- 仅在 updated_at 不存在且 update_time 存在时执行
-- ============================================================

SET @sql_rename = (
    SELECT IF(
        EXISTS(SELECT 1 FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 't_safety_appeal_record'
                 AND COLUMN_NAME = 'update_time')
        AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 't_safety_appeal_record'
                 AND COLUMN_NAME = 'updated_at'),
        'ALTER TABLE t_safety_appeal_record CHANGE COLUMN update_time updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql_rename;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
