-- ============================================================
-- V20260612_2__align_violation_type_config_schema.sql
-- 修复 2.2 暴露的 schema 漂移:
--   实际库内 t_violation_type_config 仍是历史字段
--     (type_code, type_name, trigger_retraining, enabled,
--      create_time, update_time, description),
--   与 V20260526_2 的 DDL / Java 实体 ViolationTypeConfig
--     (violation_code, violation_name, violation_level,
--      trigger_training, status, sort_order, created_at, updated_at)
--   不匹配。新增违章类型时 MyBatis-Plus 写入 violation_code 等
--   不存在的列, 抛 SQL 异常被 GlobalExceptionHandler 兜底成
--   "系统繁忙，请稍后再试"。
--   根因: application.yml baseline-version=20260606,
--   V20260526_2 被当作 baseline 跳过,从未对旧表执行 ALTER。
-- 措施: 每条 ALTER 一个 PREPARE/EXECUTE, 不写多语句字符串 (避免
--       Flyway 9 默认 MySQL parser 在字符串 ; 处误切语句).
-- 幂等: 每步独立判断, 不依赖前置步骤成功. 已对齐时所有 IF 都返 'DO 0'.
--
-- 注: dev-sts 上 t_violation_type_config 处于"半迁移"状态 (新列已被
--   V20260526_2 baseline 创建, 但类型是 varchar(255) NULL, 不是实体
--   期望的 NOT NULL / DEFAULT). 此处先 DROP 这些不匹配类型的新列, 再
--   走标准 CHANGE COLUMN rename 路径. 业务上 OK, 因为这些列如果是新建
--   表才会有数据, 旧业务用 type_code/type_name 写.
-- ============================================================

-- ============================================================
-- 0) DROP 不匹配类型的新列 (如果存在, 类型不对的: varchar(255) NULL)
-- 仅在旧列 (type_code) 仍存在时执行, 避免破坏已对齐环境
-- ============================================================
SET @has_old := (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
      AND column_name = 'type_code'
);

-- DROP violation_code (新列类型不对)
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'violation_code'),
    'ALTER TABLE t_violation_type_config DROP COLUMN violation_code',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP violation_name
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'violation_name'),
    'ALTER TABLE t_violation_type_config DROP COLUMN violation_name',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP trigger_training
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'trigger_training'),
    'ALTER TABLE t_violation_type_config DROP COLUMN trigger_training',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP created_at
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'created_at'),
    'ALTER TABLE t_violation_type_config DROP COLUMN created_at',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP updated_at
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'updated_at'),
    'ALTER TABLE t_violation_type_config DROP COLUMN updated_at',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP violation_level (新列)
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'violation_level'),
    'ALTER TABLE t_violation_type_config DROP COLUMN violation_level',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP training_hours (新列)
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'training_hours'),
    'ALTER TABLE t_violation_type_config DROP COLUMN training_hours',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP sort_order (新列)
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'sort_order'),
    'ALTER TABLE t_violation_type_config DROP COLUMN sort_order',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- DROP status (新列, 让后面从 enabled 改过来)
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'status'),
    'ALTER TABLE t_violation_type_config DROP COLUMN status',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 1) type_code → violation_code
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'type_code')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'violation_code'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN type_code violation_code VARCHAR(50) NOT NULL COMMENT ''违章代码''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2) type_name → violation_name
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'type_name')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'violation_name'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN type_name violation_name VARCHAR(100) NOT NULL COMMENT ''违章名称''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3) trigger_retraining → trigger_training
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'trigger_retraining')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'trigger_training'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN trigger_retraining trigger_training TINYINT(1) DEFAULT 1 COMMENT ''是否触发强制培训''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 4) enabled → status
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'enabled')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'status'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN enabled status VARCHAR(20) DEFAULT ''enabled'' COMMENT ''状态 enabled/disabled''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 5) create_time → created_at
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'create_time')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'created_at'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN create_time created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 6) update_time → updated_at
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'update_time')
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'updated_at'),
    'ALTER TABLE t_violation_type_config CHANGE COLUMN update_time updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 7) 补 violation_level
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'violation_level'),
    'ALTER TABLE t_violation_type_config ADD COLUMN violation_level VARCHAR(20) NOT NULL DEFAULT ''minor'' COMMENT ''级别 minor/major/critical'' AFTER violation_name',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 8) 补 training_hours
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'training_hours'),
    'ALTER TABLE t_violation_type_config ADD COLUMN training_hours INT DEFAULT 0 COMMENT ''触发培训学时'' AFTER trigger_training',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 9) 补 sort_order
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.columns
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND column_name = 'sort_order'),
    'ALTER TABLE t_violation_type_config ADD COLUMN sort_order INT DEFAULT 0 COMMENT ''排序'' AFTER status',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 10) 删 description (新 schema 不再使用)
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'description'),
    'ALTER TABLE t_violation_type_config DROP COLUMN description',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 11) status 数据迁移: TINYINT 1/0 -> 'enabled'/'disabled'
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND column_name = 'status'),
    'UPDATE t_violation_type_config SET status = CASE WHEN status = ''1'' THEN ''enabled'' WHEN status = ''0'' THEN ''disabled'' WHEN status IS NULL OR status = '''' THEN ''enabled'' ELSE status END WHERE status NOT IN (''enabled'', ''disabled'')',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 12) 索引对齐: 删旧 type_code / idx_type_code
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.statistics
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND index_name = 'type_code'),
    'ALTER TABLE t_violation_type_config DROP INDEX `type_code`',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    @has_old > 0
    AND EXISTS(SELECT 1 FROM information_schema.statistics
               WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                 AND index_name = 'idx_type_code'),
    'ALTER TABLE t_violation_type_config DROP INDEX `idx_type_code`',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 13) 索引对齐: 加新名 (uk_violation_code / idx_status / idx_violation_level)
-- ============================================================
SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.statistics
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND index_name = 'uk_violation_code'),
    'ALTER TABLE t_violation_type_config ADD UNIQUE KEY `uk_violation_code` (`violation_code`)',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.statistics
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND index_name = 'idx_status'),
    'ALTER TABLE t_violation_type_config ADD KEY `idx_status` (`status`)',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    @has_old > 0
    AND NOT EXISTS(SELECT 1 FROM information_schema.statistics
                   WHERE table_schema = DATABASE() AND table_name = 't_violation_type_config'
                     AND index_name = 'idx_violation_level'),
    'ALTER TABLE t_violation_type_config ADD KEY `idx_violation_level` (`violation_level`)',
    'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
