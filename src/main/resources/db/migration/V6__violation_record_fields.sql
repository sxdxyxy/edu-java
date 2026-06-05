-- =====================================================
-- V6: 违章记录字段扩展
-- 创建时间: 2026-05-31
-- 说明: 为违章即扣分流程新增字段
-- =====================================================

-- violation_records 表新增字段
ALTER TABLE violation_records
ADD COLUMN IF NOT EXISTS person_id BIGINT COMMENT '人员ID（xm_person.id）',
ADD COLUMN IF NOT EXISTS deduct_amount INT COMMENT '本次扣分分值',
ADD COLUMN IF NOT EXISTS trigger_retraining BOOLEAN DEFAULT FALSE COMMENT '是否触发再培训',
ADD COLUMN IF NOT EXISTS retraining_record_id BIGINT COMMENT '关联的再培训记录ID',
ADD COLUMN IF NOT EXISTS operator_id BIGINT COMMENT '操作人ID（录入人）';

-- 为 person_id 添加索引
ALTER TABLE violation_records
ADD INDEX IF NOT EXISTS idx_violation_person_id (person_id);

-- 状态说明：
-- pending       - 待处理（违章录入，即时扣分后状态）
-- processed     - 已处理（处理完成）
-- appealed      - 申诉中
-- appeal_approved - 申诉通过（已返还积分）
-- appeal_rejected - 申诉驳回（维持原判）

-- =====================================================
-- 完成
-- =====================================================
