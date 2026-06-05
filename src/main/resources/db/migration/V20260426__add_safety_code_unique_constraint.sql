-- =====================================================
-- 安全码表添加唯一约束
-- 创建时间：2026-04-26
-- 说明：添加 person_id + project_id 唯一约束，确保一人一项目一安全码
-- =====================================================

-- 添加 person_id 和 org_id 字段（如果不存在）
ALTER TABLE `safety_codes`
ADD COLUMN IF NOT EXISTS `person_id` BIGINT COMMENT '人员 ID（关联 xm_person.id）' AFTER `user_id`,
ADD COLUMN IF NOT EXISTS `org_id` BIGINT COMMENT '组织机构 ID' AFTER `project_id`;

-- 添加唯一约束：同一人员在同一项目只能有一条安全码记录
-- 先删除可能存在的旧约束（如果重建）
-- ALTER TABLE `safety_codes` DROP INDEX IF EXISTS `uk_person_project`;

-- 添加唯一约束
ALTER TABLE `safety_codes`
ADD CONSTRAINT `uk_person_project` UNIQUE (`person_id`, `project_id`);

-- 添加索引用于查询
ALTER TABLE `safety_codes`
ADD INDEX `idx_person_id` (`person_id`),
ADD INDEX `idx_org_id` (`org_id`);
