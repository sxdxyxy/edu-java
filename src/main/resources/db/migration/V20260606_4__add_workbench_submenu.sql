-- =====================================================
-- V20260606_4: 智理安全下加"我的工作台"子菜单
-- 创建时间: 2026-06-06
-- 说明: Phase 5 方案 §5.2 角色工作台 (SafetyOfficer/Supervisor/TrainingAdmin/ProjectManager)
--       之前内嵌在 SafetyDashboard 顶部,用户找不到,改为独立子菜单
--       路由 /safety/dashboard?workbench=true 锚定到工作台区域
-- 兼容: MySQL 8 不支持 ADD COLUMN IF NOT EXISTS
--       用 session 变量 + PREPARE 实现幂等
-- =====================================================

-- 幂等插入"我的工作台"子菜单 (MySQL 8 兼容写法)
SET @pid_dawa := (SELECT id FROM sys_menu WHERE code = 'dawa' LIMIT 1);

SET @exists := (SELECT COUNT(*) FROM sys_menu WHERE code = 'safety_workbench');
SET @int_code := 5;  -- 排在 safety_dashboard 之前
SET @sql := IF(
    @exists = 0 AND @pid_dawa IS NOT NULL,
    'INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete) VALUES (''我的工作台'', ''safety_workbench'', 5, ' || @pid_dawa || ', 1, ''/safety/dashboard?workbench=true'', ''safety/SafetyDashboard'', ''safety:workbench:view'', ''appstore'', 1, NOW(), 0)',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
