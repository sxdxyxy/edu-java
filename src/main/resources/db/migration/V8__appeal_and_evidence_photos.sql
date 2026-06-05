-- =====================================================
-- V8: 申诉流程 + 证据照片字段
-- 创建时间: 2026-05-31
-- 说明: 违章记录新增 person_id / evidence_photos / status，
--       并创建申诉记录表 appeal_records
-- =====================================================

-- =====================================================
-- 1. violation_records 新增字段（补充 Phase 2/3 未生效的改动）
-- =====================================================
ALTER TABLE violation_records
ADD COLUMN IF NOT EXISTS person_id BIGINT COMMENT '人员ID（xm_person.id）',
ADD COLUMN IF NOT EXISTS deduct_amount INT COMMENT '本次扣分分值',
ADD COLUMN IF NOT EXISTS trigger_retraining BOOLEAN DEFAULT FALSE COMMENT '是否触发再培训',
ADD COLUMN IF NOT EXISTS retraining_record_id BIGINT COMMENT '关联再培训记录ID',
ADD COLUMN IF NOT EXISTS operator_id BIGINT COMMENT '操作人ID（录入人）';

-- 修改 evidence_photos 为 JSON 类型（前端传 JSON 数组）
ALTER TABLE violation_records
MODIFY COLUMN evidence_photos JSON COMMENT '证据照片URL列表（JSON数组）';

-- 添加索引
ALTER TABLE violation_records
ADD INDEX IF NOT EXISTS idx_violation_person_id (person_id);

-- =====================================================
-- 2. 申诉记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS t_safety_appeal_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    violation_record_id BIGINT NOT NULL COMMENT '关联违章记录ID',
    person_id BIGINT NOT NULL COMMENT '申诉人（作业人员）ID',
    appeal_reason TEXT NOT NULL COMMENT '申诉理由',
    appeal_evidence JSON COMMENT '申诉补充证据照片URL列表（JSON数组）',
    appeal_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申诉时间',
    reviewer_id BIGINT COMMENT '审批人ID（安全主管）',
    review_result VARCHAR(20) DEFAULT 'pending' COMMENT '审批结果 pending/approved/rejected',
    review_comment TEXT COMMENT '审批意见',
    review_time DATETIME COMMENT '审批时间',
    score_restored INT DEFAULT 0 COMMENT '返还积分分值（申诉通过时）',
    score_restored_at DATETIME COMMENT '积分返还时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_violation_record_id (violation_record_id),
    INDEX idx_person_id (person_id),
    INDEX idx_review_result (review_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全违章申诉记录表';

-- =====================================================
-- 完成
-- =====================================================
