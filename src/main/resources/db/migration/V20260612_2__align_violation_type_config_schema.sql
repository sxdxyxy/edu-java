-- ============================================================
-- V20260612__align_violation_type_config_schema.sql
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
-- 措施: 若检测到旧 schema 残留 (type_code 仍存在),做列重命名 +
--       类型调整 + 补齐缺失列 + 数据迁移;已对齐则 no-op。
-- 幂等: MySQL 8 不支持 RENAME COLUMN IF EXISTS,使用 information_schema
--       + PREPARE/EXECUTE 串实现幂等切换。
-- ============================================================
-- 检测是否仍是旧 schema (type_code 存在)
SET @is_old_schema := (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 't_violation_type_config'
      AND column_name = 'type_code'
);
-- 旧 schema 改造(单 PREPARE 包整段 ALTER + 数据迁移 + 索引对齐)
SET @ddl := IF(
    @is_old_schema > 0,
    '
    -- 1. 列重命名 + 类型/默认值调整 (保留存量数据)
    ALTER TABLE t_violation_type_config
        CHANGE COLUMN type_code violation_code VARCHAR(50) NOT NULL COMMENT ''违章代码'',
        CHANGE COLUMN type_name violation_name VARCHAR(100) NOT NULL COMMENT ''违章名称'',
        CHANGE COLUMN trigger_retraining trigger_training TINYINT(1) DEFAULT 1 COMMENT ''是否触发强制培训'',
        CHANGE COLUMN enabled status VARCHAR(20) DEFAULT ''enabled'' COMMENT ''状态 enabled/disabled'',
        CHANGE COLUMN create_time created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        CHANGE COLUMN update_time updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'';
    -- 2. 补齐 NEW schema 缺失列 (V20260526_2 设计)
    ALTER TABLE t_violation_type_config
        ADD COLUMN violation_level VARCHAR(20) NOT NULL DEFAULT ''minor'' COMMENT ''级别 minor/major/critical'' AFTER violation_name,
        ADD COLUMN training_hours INT DEFAULT 0 COMMENT ''触发培训学时'' AFTER trigger_training,
        ADD COLUMN sort_order INT DEFAULT 0 COMMENT ''排序'' AFTER status;
    -- 3. 删除冗余字段 description (新 schema 不再使用)
    ALTER TABLE t_violation_type_config DROP COLUMN description;
    -- 4. status 数据迁移: TINYINT 1/0 -> ''enabled''/''disabled''
    UPDATE t_violation_type_config
        SET status = CASE
            WHEN status = ''1'' THEN ''enabled''
            WHEN status = ''0'' THEN ''disabled''
            WHEN status IS NULL OR status = '''' THEN ''enabled''
            ELSE status
        END
        WHERE status NOT IN (''enabled'', ''disabled'');
    -- 5. 索引对齐 V20260526_2 设计
    --    旧 type_code UNIQUE / idx_type_code 列已重命名,先删旧名再加新名
    ALTER TABLE t_violation_type_config DROP INDEX `type_code`;
    ALTER TABLE t_violation_type_config DROP INDEX `idx_type_code`;
    ALTER TABLE t_violation_type_config ADD UNIQUE KEY `uk_violation_code` (`violation_code`);
    ALTER TABLE t_violation_type_config ADD KEY `idx_status` (`status`);
    ALTER TABLE t_violation_type_config ADD KEY `idx_violation_level` (`violation_level`);
    ',
    'SELECT ''t_violation_type_config already aligned, skip'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
