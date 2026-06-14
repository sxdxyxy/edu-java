-- =====================================================
-- V20260621: 清理 3 张半迁移表的冗余 created_at/updated_at 列
-- 创建时间: 2026-06-14
-- 说明: V20260618 补 created_at/updated_at 时, 这 3 张表已存在
--   create_time/update_time (BaseEntity 硬编码). 两套并存导致 INSERT
--   时 2 套审计字段都被写, 数据冗余 + 容易"哪个时间对不上"
--   实体字段分析:
--     - CourseAdmissionRule  : extends BaseEntity (硬编码 create_time) +
--                             自身有 createdAt/updatedAt (无 @TableField,
--                             自动映射 created_at/updated_at)
--     - SafetyRetrainingRecord: 同上
--     - SafetyScoreAccount  : 不 extends BaseEntity, 但显式 @TableField
--                             同时映射 create_time/create_by/.../created_at
--                             两套共存
-- 措施: 保留 create_time/update_time (BaseEntity 唯一来源), 删掉
--   created_at/updated_at (Java 字段在 drop 后会被 MyBatis-Plus 警告但不报错)
-- 兼容: 3 表实测在 dev-sts 上均为空, 0 数据丢失风险
-- 幂等: INFORMATION_SCHEMA 判存在性 + PREPARE/EXECUTE
-- =====================================================

-- t_course_admission_rule
SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_course_admission_rule DROP COLUMN created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_course_admission_rule' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_course_admission_rule DROP COLUMN updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_course_admission_rule' AND COLUMN_NAME = 'updated_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- t_safety_retraining_record
SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_safety_retraining_record DROP COLUMN created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_safety_retraining_record DROP COLUMN updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_retraining_record' AND COLUMN_NAME = 'updated_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- t_safety_score_account
SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_safety_score_account DROP COLUMN created_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'created_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(COUNT(*) > 0, 'ALTER TABLE t_safety_score_account DROP COLUMN updated_at', 'DO 0') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_safety_score_account' AND COLUMN_NAME = 'updated_at');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
