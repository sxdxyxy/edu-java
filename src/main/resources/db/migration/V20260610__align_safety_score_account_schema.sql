-- ============================================================
-- V20260610__align_safety_score_account_schema.sql
-- 修 D1-A 暴露的 schema 漂移:
--   库内 t_safety_score_account 只有 8 列,但 SafetyScoreAccount 实体期望 21 列,
--   V20260526_3 因 flyway baseline=20260606 从未执行
-- 措施: ALTER TABLE 补齐缺失列(列名按 V20260526_3 原 DDL + 实体 createBy/updateBy 模式)
-- ============================================================

-- 缺失列: user_id, project_id, work_type, person_work_type(已存在,跳过),
--          initial_score, annual_reset_date, status, created_at, updated_at
--          create_by, update_by, is_delete, delete_by, delete_time, delete_reason, remark

-- 1) 必补的列
ALTER TABLE t_safety_score_account
    ADD COLUMN user_id BIGINT NULL COMMENT '用户ID(关联sys_user)' AFTER person_id,
    ADD COLUMN project_id BIGINT NULL COMMENT '项目部ID' AFTER user_id,
    ADD COLUMN work_type VARCHAR(50) NOT NULL DEFAULT 'worker' COMMENT '岗位类型 worker/specialized/safety_admin' AFTER project_id,
    ADD COLUMN initial_score INT NOT NULL DEFAULT 12 COMMENT '初始积分(12/15/10)' AFTER work_type,
    ADD COLUMN annual_reset_date DATE NULL COMMENT '年度清零日期' AFTER current_score,
    ADD COLUMN status VARCHAR(20) DEFAULT 'active' COMMENT '状态 active/frozen' AFTER annual_reset_date,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER status,
    ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER created_at,
    ADD COLUMN create_by BIGINT NULL COMMENT '创建人' AFTER updated_at,
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人' AFTER create_by,
    ADD COLUMN update_time DATETIME NULL COMMENT '更新时间(MyBatis-Plus 字段)' AFTER update_by,
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_time,
    ADD COLUMN delete_by BIGINT NULL COMMENT '删除人' AFTER is_delete,
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间' AFTER delete_by,
    ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT '删除原因' AFTER delete_time,
    ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注' AFTER delete_reason;

-- 2) 加索引(对齐 V20260526_3 原 DDL 设计)
ALTER TABLE t_safety_score_account
    ADD KEY idx_user_id (user_id),
    ADD KEY idx_project_id (project_id),
    ADD KEY idx_status (status);

-- 3) 历史 person_work_type 已有,改名注释? 不动 schema
--    safety_code 字段是较新业务字段,保留

-- 4) 老 person_id 唯一键已经存在 (uk_person_id),保留
