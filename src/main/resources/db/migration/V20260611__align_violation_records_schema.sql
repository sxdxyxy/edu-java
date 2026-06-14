-- ============================================================
-- V20260611__align_violation_records_schema.sql
-- 修 D2 暴露的 violation_records schema 漂移:
--   Mapper resultMap 期望下划线列名(project_id/org_id/violation_type/...)
--   实际表只有 13 列,缺核心列
-- 措施: 补齐缺失列(全部加到表尾,不依赖中间列,避免 AFTER 引用失败)
-- ============================================================
--
-- 2026-06-14 修订: 改为幂等写法(INFORMATION_SCHEMA 判列/索引存在性).
--   原因: dev-sts 上 violation_records 表已被手动/V20260618 提前补齐列 + 索引,
--   直接 ALTER 会报 "Duplicate column name",启动卡 Flyway 失败.
--   切到幂等后, 不管列是否已存在都跑得通.
-- ============================================================

-- 缺失列补齐(幂等: 仅在列不存在时 ADD)

-- project_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN project_id BIGINT NULL COMMENT ''项目部ID''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'project_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- org_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN org_id BIGINT NULL COMMENT ''机构ID''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'org_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- violation_type
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN violation_type VARCHAR(50) NULL COMMENT ''违章类型''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'violation_type');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- severity
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN severity VARCHAR(20) NULL COMMENT ''严重程度''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'severity');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- description
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN description VARCHAR(500) NULL COMMENT ''违章描述''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'description');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- location
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN location VARCHAR(200) NULL COMMENT ''违章地点''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'location');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- handler_id
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN handler_id BIGINT NULL COMMENT ''处理人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'handler_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- processed_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN processed_at DATETIME NULL COMMENT ''处理时间''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'processed_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- created_at
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN create_by BIGINT NULL COMMENT ''创建人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'create_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN update_by BIGINT NULL COMMENT ''更新人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'update_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_delete
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除 0/1''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'is_delete');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_by
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN delete_by BIGINT NULL COMMENT ''删除人''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'delete_by');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_time
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN delete_time DATETIME NULL COMMENT ''删除时间''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'delete_time');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- delete_reason
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT ''删除原因''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'delete_reason');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- remark
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD COLUMN remark VARCHAR(500) NULL COMMENT ''备注''', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND COLUMN_NAME = 'remark');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 索引补齐(幂等)
SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD KEY idx_user_id (user_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND INDEX_NAME = 'idx_user_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD KEY idx_project_id (project_id)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND INDEX_NAME = 'idx_project_id');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE violation_records ADD KEY idx_status (status)', 'DO 0') FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'violation_records' AND INDEX_NAME = 'idx_status');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
