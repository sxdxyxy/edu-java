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

-- ============================================================
-- 1) t_safety_score_transaction: 补 13 列
-- ============================================================
ALTER TABLE t_safety_score_transaction
    ADD COLUMN change_type VARCHAR(32) NULL COMMENT '变更类型 deduct/restore/reset/adjust' AFTER account_id,
    ADD COLUMN before_score INT NULL COMMENT '变更前积分' AFTER change_amount,
    ADD COLUMN after_score INT NULL COMMENT '变更后积分' AFTER before_score,
    ADD COLUMN trigger_reason VARCHAR(255) NULL COMMENT '触发原因描述' AFTER after_score,
    ADD COLUMN violation_record_id BIGINT NULL COMMENT '关联违章记录' AFTER trigger_reason,
    ADD COLUMN retraining_record_id BIGINT NULL COMMENT '关联再培训记录' AFTER violation_record_id,
    ADD COLUMN remarks VARCHAR(500) NULL COMMENT '备注' AFTER retraining_record_id,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER remarks,
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人' AFTER created_at,
    ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MyBatis updateTime 字段' AFTER update_by,
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_time,
    ADD COLUMN delete_by BIGINT NULL COMMENT '删除人' AFTER is_delete,
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间' AFTER delete_by,
    ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT '删除原因' AFTER delete_time,
    ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注' AFTER delete_reason;

-- 索引
ALTER TABLE t_safety_score_transaction
    ADD KEY idx_change_type (change_type),
    ADD KEY idx_violation_record_id (violation_record_id);

-- ============================================================
-- 2) t_safety_retraining_record: 补 15 列 + 改名 update_time→updated_at
-- ============================================================
ALTER TABLE t_safety_retraining_record
    ADD COLUMN violation_record_id BIGINT NULL COMMENT '关联违章记录' AFTER person_id,
    ADD COLUMN violation_code VARCHAR(50) NULL COMMENT '违章代码' AFTER violation_record_id,
    ADD COLUMN training_course_id BIGINT NULL COMMENT '培训课程ID' AFTER violation_code,
    ADD COLUMN training_hours INT NULL COMMENT '培训学时' AFTER training_course_id,
    ADD COLUMN start_date DATE NULL COMMENT '开始日期' AFTER status,
    ADD COLUMN end_date DATE NULL COMMENT '结束日期' AFTER start_date,
    ADD COLUMN operator_id BIGINT NULL COMMENT '操作人' AFTER confirmed_at,
    ADD COLUMN remarks VARCHAR(500) NULL COMMENT '备注' AFTER operator_id,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER remarks,
    ADD COLUMN create_by BIGINT NULL COMMENT '创建人' AFTER created_at,
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人' AFTER create_by,
    ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MyBatis updateTime 字段' AFTER update_by,
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_time,
    ADD COLUMN delete_by BIGINT NULL COMMENT '删除人' AFTER is_delete,
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间' AFTER delete_by,
    ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT '删除原因' AFTER delete_time,
    ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注' AFTER delete_reason;

-- 改名 update_time → updated_at(实体字段 updatedAt)
ALTER TABLE t_safety_retraining_record
    CHANGE COLUMN update_time updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 索引
ALTER TABLE t_safety_retraining_record
    ADD KEY idx_violation_record_id (violation_record_id),
    ADD KEY idx_training_course_id (training_course_id);

-- ============================================================
-- 3) t_course_admission_rule: 补 3 审计字段
-- ============================================================
ALTER TABLE t_course_admission_rule
    ADD COLUMN create_by BIGINT NULL COMMENT '创建人' AFTER updated_at,
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人' AFTER create_by,
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_by;

-- ============================================================
-- 4) t_safety_appeal_record: 补 created_at + rename update_time + 3 审计
-- ============================================================
ALTER TABLE t_safety_appeal_record
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER update_time,
    ADD COLUMN create_by BIGINT NULL COMMENT '创建人' AFTER created_at,
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人' AFTER create_by,
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1' AFTER update_by;

-- 改名 update_time → updated_at(实体字段 updatedAt)
ALTER TABLE t_safety_appeal_record
    CHANGE COLUMN update_time updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================================
-- 5) access_records: 补 1 关联字段
-- ============================================================
ALTER TABLE access_records
    ADD COLUMN org_id BIGINT NULL COMMENT '组织机构ID' AFTER project_id;

-- 索引
ALTER TABLE access_records
    ADD KEY idx_org_id (org_id);
