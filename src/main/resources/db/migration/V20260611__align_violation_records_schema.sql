-- ============================================================
-- V20260611__align_violation_records_schema.sql
-- 修 D2 暴露的 violation_records schema 漂移:
--   Mapper resultMap 期望下划线列名(project_id/org_id/violation_type/...)
--   实际表只有 13 列,缺核心列
-- 措施: 补齐缺失列(全部加到表尾,不依赖中间列,避免 AFTER 引用失败)
-- ============================================================

ALTER TABLE violation_records
    ADD COLUMN project_id BIGINT NULL COMMENT '项目部ID',
    ADD COLUMN org_id BIGINT NULL COMMENT '机构ID',
    ADD COLUMN violation_type VARCHAR(50) NULL COMMENT '违章类型',
    ADD COLUMN severity VARCHAR(20) NULL COMMENT '严重程度',
    ADD COLUMN description VARCHAR(500) NULL COMMENT '违章描述',
    ADD COLUMN location VARCHAR(200) NULL COMMENT '违章地点',
    ADD COLUMN handler_id BIGINT NULL COMMENT '处理人',
    ADD COLUMN processed_at DATETIME NULL COMMENT '处理时间',
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN create_by BIGINT NULL COMMENT '创建人',
    ADD COLUMN update_by BIGINT NULL COMMENT '更新人',
    ADD COLUMN is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0/1',
    ADD COLUMN delete_by BIGINT NULL COMMENT '删除人',
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间',
    ADD COLUMN delete_reason VARCHAR(255) NULL COMMENT '删除原因',
    ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注';

-- 索引(原 DDL 设计意图)
ALTER TABLE violation_records
    ADD KEY idx_user_id (user_id),
    ADD KEY idx_project_id (project_id),
    ADD KEY idx_status (status);
