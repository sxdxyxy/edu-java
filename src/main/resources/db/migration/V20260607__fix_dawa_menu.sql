-- =====================================================
-- V20260607: 修复 dev-sts 菜单结构 - 智理安全(da wa)菜单补齐
-- 创建时间: 2026-06-07
-- 说明: 之前 V20260402_3 / V20260530 等迁移都假设 sys_menu.code='dawa' 已存在,
--       但 dev-sts 上 dawa 顶级菜单从未插入成功,导致所有 dawa_* 子菜单被 IF() 跳过。
--       此迁移:
--         1. 补 dawa 顶级菜单
--         2. 把原本挂在 pid=308 (safety 顶级) 的 5 个子菜单挪到 dawa 下
--         3. 把 retraining / violation_type 从 pid=6 (更老的不存在菜单) 挪到 dawa 下
--         4. 隐藏老的 safety 顶级菜单(若还存在)
--         5. 补 dawa_dashboard 默认首页 + safety_workbench 我的工作台
-- 兼容: MySQL 8, 用 session 变量
-- =====================================================

-- 1. 补 dawa 顶级菜单(若不存在)
--    component=RouteView: 前端 generator-routers.js 已注册此 key;
--    写 'Layout' 会触发 fallback 路径 `() => import('@/views/Layout')` 加载失败,
--    进而整个 dawa 路由树都无法渲染
SET @dawa_exists := (SELECT COUNT(*) FROM sys_menu WHERE code = 'dawa');
SET @sql := IF(
    @dawa_exists = 0,
    'INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete) VALUES (''智理安全'', ''dawa'', 20, 0, 0, ''/dawa'', ''RouteView'', '''', ''safety-certificate'', 1, NOW(), 0)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 1b. 兜底:历史数据里 dawa.component 写成 Layout,纠正成 RouteView
UPDATE sys_menu SET component = 'RouteView' WHERE code = 'dawa' AND component = 'Layout';

-- 2. 取 dawa id
SET @dawa_id := (SELECT id FROM sys_menu WHERE code = 'dawa' LIMIT 1);

-- 3. 把 5 个菜单从 pid=308 挪到 dawa
UPDATE sys_menu SET pid = @dawa_id, int_code = 30 WHERE code = 'safety_code' AND pid = 308;
UPDATE sys_menu SET pid = @dawa_id, int_code = 40 WHERE code = 'safety_qualification' AND pid = 308;
UPDATE sys_menu SET pid = @dawa_id, int_code = 50 WHERE code = 'safety_violation' AND pid = 308;
UPDATE sys_menu SET pid = @dawa_id, int_code = 60 WHERE code = 'safety_access' AND pid = 308;
UPDATE sys_menu SET pid = @dawa_id, int_code = 70 WHERE code = 'safety_assessment' AND pid = 308;

-- 4. retraining / violation_type: 原来可能挂在 pid=6 或其它错误 pid, 统一改到 dawa
UPDATE sys_menu SET pid = @dawa_id, int_code = 80 WHERE code = 'safety_retraining' AND pid != @dawa_id;
UPDATE sys_menu SET pid = @dawa_id, int_code = 90 WHERE code = 'safety_violation_type' AND pid != @dawa_id;

-- 5. 隐藏老的 safety 顶级菜单
UPDATE sys_menu SET visible = 0 WHERE code = 'safety';

-- 6. 删空占位
DELETE FROM sys_menu WHERE code = 'safety:view' AND name = '安全监管.view';

-- 7. 补 dawa_dashboard 默认首页(让"智理安全"展开后有页面)
SET @dash_exists := (SELECT COUNT(*) FROM sys_menu WHERE code = 'dawa_dashboard');
SET @sql := IF(
    @dash_exists = 0 AND @dawa_id IS NOT NULL,
    'INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete) VALUES (''安全仪表盘'', ''dawa_dashboard'', 1, ' || @dawa_id || ', 1, ''/dawa/dashboard'', ''dawa/SafetyDashboard'', '''', ''dashboard'', 1, NOW(), 0)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 8. 补 safety_workbench 我的工作台
SET @wb_exists := (SELECT COUNT(*) FROM sys_menu WHERE code = 'safety_workbench');
SET @sql := IF(
    @wb_exists = 0 AND @dawa_id IS NOT NULL,
    'INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete) VALUES (''我的工作台'', ''safety_workbench'', 5, ' || @dawa_id || ', 1, ''/safety/dashboard?workbench=true'', ''safety/SafetyDashboard'', ''safety:workbench:view'', ''appstore'', 1, NOW(), 0)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
