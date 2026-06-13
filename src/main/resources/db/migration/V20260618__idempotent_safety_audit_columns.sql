-- =====================================================
-- V20260618: 幂等补全 safety 模块审计列 (兜底)
-- 说明: V20260610 / V20260612 在 dev-sts 上可能因 baseline 跳号 / strict mode 失败
--   此处用 ALTER TABLE ... ADD COLUMN IF NOT EXISTS 幂等补齐
-- 注: MySQL 8.0.29+ 支持 ADD COLUMN IF NOT EXISTS
--     旧版本 (5.7) 需改用存储过程 try-catch
-- =====================================================

-- 1) t_safety_score_account: 补可能被跳过的列
-- V20260610 的 AFTER 子句是顺序追加的,如果中间某条失败,后续列都可能缺失
-- 用 IF NOT EXISTS 兜底,AFTER 子句按 V20260610 定义的最终顺序
ALTER TABLE t_safety_score_account
    ADD COLUMN IF NOT EXISTS user_id BIGINT NULL COMMENT '用户ID(关联sys_user)' AFTER person_id,
    ADD COLUMN IF NOT EXISTS project_id BIGINT NULL COMMENT '项目部ID' AFTER user_id,
    ADD COLUMN IF NOT EXISTS work_type VARCHAR(50) NOT NULL DEFAULT 'worker' COMMENT '岗位类型 worker/specialized/safety_admin' AFTER project_id,
    ADD COLUMN IF NOT EXISTS initial_score INT NOT NULL DEFAULT 12 COMMENT '初始积分(12/15/10)' AFTER work_type,
    ADD COLUMN IF NOT EXISTS annual_reset_date DATE NULL COMMENT '年度清零日期' AFTER current_score,
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'active' COMMENT '状态 active/frozen' AFTER annual_reset_date,
    ADD COLUMN IF NOT EXISTS created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER status,
    ADD COLUMN IF NOT EXISTS updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER created_at,
    ADD COLUMN IF NOT EXISTS create_by BIGINT NULL COMMENT '创建人' AFTER updated_at,
    ADD COLUMN IF NOT EXISTS update_by BIGINT NULL COMMENT '更新人' AFTER create_by,
    ADD COLUMN IF NOT EXISTS update_time DATETIME NULL COMMENT '更新时间(MyBatis-Plus 字段)' AFTER update_by,
    ADD COLUMN IF NOT EXISTS is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_time,
    ADD COLUMN IF NOT EXISTS delete_by BIGINT NULL COMMENT '删除人' AFTER is_delete,
    ADD COLUMN IF NOT EXISTS delete_time DATETIME NULL COMMENT '删除时间' AFTER delete_by,
    ADD COLUMN IF NOT EXISTS delete_reason VARCHAR(255) NULL COMMENT '删除原因' AFTER delete_time,
    ADD COLUMN IF NOT EXISTS remark VARCHAR(500) NULL COMMENT '备注' AFTER delete_reason;

-- 2) 索引补齐 (V20260610 末尾)
ALTER TABLE t_safety_score_account
    ADD INDEX IF NOT EXISTS idx_user_id (user_id),
    ADD INDEX IF NOT EXISTS idx_project_id (project_id),
    ADD INDEX IF NOT EXISTS idx_status (status);

-- 3) t_safety_appeal_record: 补齐列
-- 注意: V8 创建时无 update_time, V20260612 的 "ADD COLUMN created_at AFTER update_time"
--   会在新建 dev-sts 上因找不到 update_time 而失败, 导致 created_at / create_by / update_by / is_delete 全缺失
-- 这里不加 AFTER 子句 (追加到表尾) 避免再次依赖不存在的列
ALTER TABLE t_safety_appeal_record
    ADD COLUMN IF NOT EXISTS created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN IF NOT EXISTS create_by BIGINT NULL COMMENT '创建人',
    ADD COLUMN IF NOT EXISTS update_by BIGINT NULL COMMENT '更新人',
    ADD COLUMN IF NOT EXISTS is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1';

-- 4) t_safety_appeal_record: 如果 V20260612 的 "update_time -> updated_at" rename 没执行,补一次
-- 实体字段是 updatedAt;如果列名仍是 update_time,统一改名为 updated_at
-- 仅在 updated_at 不存在且 update_time 存在时执行
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
