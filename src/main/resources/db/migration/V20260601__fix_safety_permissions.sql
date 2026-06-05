-- =====================================================
-- V20260601: 修复安全监管模块权限配置
-- 创建时间: 2026-06-01
-- 说明: 修正菜单权限标识，与后端 @PreAuthorize 注解匹配
-- =====================================================

-- 再培训管理权限修正
UPDATE sys_menu SET permission = 'safety:retraining:list' WHERE code = 'safety_retraining';

-- 违章类型配置权限修正
UPDATE sys_menu SET permission = 'safety:violationtype:list' WHERE code = 'safety_violation_type';

-- =====================================================
-- 完成
-- =====================================================
