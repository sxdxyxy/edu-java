-- =====================================================
-- V7: 再培训记录字段扩展
-- 创建时间: 2026-05-31
-- 说明: 再培训与课程体系联动，管理员确认后自动恢复积分
-- =====================================================

-- t_safety_retraining_record 表新增字段
ALTER TABLE t_safety_retraining_record
ADD COLUMN IF NOT EXISTS training_plan_id BIGINT COMMENT '匹配培训计划ID',
ADD COLUMN IF NOT EXISTS confirmed_by BIGINT COMMENT '确认人ID',
ADD COLUMN IF NOT EXISTS confirmed_at DATETIME COMMENT '确认时间';

-- =====================================================
-- 完成
-- =====================================================
