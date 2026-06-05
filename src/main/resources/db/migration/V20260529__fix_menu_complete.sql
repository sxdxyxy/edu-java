-- V20260529__fix_menu_complete.sql
-- 菜单完整修复：组件映射、系统菜单、图标、无效菜单

START TRANSACTION;

-- ============================================================
-- 1. 修复菜单组件（与前端 router 匹配）
-- ============================================================

-- 项目组织
UPDATE sys_menu SET component = 'project/engineering/EngineeringProjectList' WHERE code = 'project_engineering';
UPDATE sys_menu SET component = 'org/OrgList' WHERE code = 'org';
UPDATE sys_menu SET component = 'person/PersonList' WHERE code = 'person';

-- 培训管理
UPDATE sys_menu SET component = 'project/trainPlan/TrainPlanList' WHERE code = 'train_plan';
UPDATE sys_menu SET component = 'project/ProjectList' WHERE code = 'train_project';
UPDATE sys_menu SET component = 'project/terminalTrain/TerminalTrainList' WHERE code = 'train_terminal';

-- 资源中心
UPDATE sys_menu SET component = 'course/CourseList' WHERE code = 'resource_course';
UPDATE sys_menu SET component = 'course/question/QuestionList' WHERE code = 'resource_question';

-- 培训档案
UPDATE sys_menu SET component = 'archives/user/Index' WHERE code = 'archives_user';
UPDATE sys_menu SET component = 'archives/trainingRecords/Index' WHERE code = 'archives_training';

-- 运营中心
UPDATE sys_menu SET component = 'data/DataStatistic' WHERE code = 'operation_stats';
UPDATE sys_menu SET component = 'appNews/list' WHERE code = 'operation_news';
UPDATE sys_menu SET component = 'appNotice/list' WHERE code = 'operation_notice';
UPDATE sys_menu SET component = 'banner/bannerList' WHERE code = 'operation_banner';

-- ============================================================
-- 2. 添加系统管理子菜单
-- ============================================================

INSERT INTO sys_menu (name, code, icon, pid, type, router, component, visible, int_code, create_time, update_time)
SELECT '用户管理', 'sys_user', 'user', (SELECT id FROM sys_menu WHERE code = 'system'), 0, '/system/user', 'security/user/UserList', 1, 71, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'sys_user');

INSERT INTO sys_menu (name, code, icon, pid, type, router, component, visible, int_code, create_time, update_time)
SELECT '角色管理', 'sys_role', 'team', (SELECT id FROM sys_menu WHERE code = 'system'), 0, '/system/role', 'security/role/RoleList', 1, 72, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'sys_role');

INSERT INTO sys_menu (name, code, icon, pid, type, router, component, visible, int_code, create_time, update_time)
SELECT '菜单管理', 'sys_menu', 'menu', (SELECT id FROM sys_menu WHERE code = 'system'), 0, '/system/menu', 'security/menu/MenuList', 1, 73, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'sys_menu');

INSERT INTO sys_menu (name, code, icon, pid, type, router, component, visible, int_code, create_time, update_time)
SELECT '字典管理', 'sys_dictionary', 'book', (SELECT id FROM sys_menu WHERE code = 'system'), 0, '/system/dictionary', 'sys/dictionary/DictionaryList', 1, 74, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'sys_dictionary');

-- ============================================================
-- 3. 隐藏无效菜单（无对应组件）
-- ============================================================

UPDATE sys_menu SET visible = 0 WHERE code = 'project_dept';   -- 项目部
UPDATE sys_menu SET visible = 0 WHERE code = 'archives_job';  -- 岗位档案
UPDATE sys_menu SET visible = 0 WHERE code = 'resource_file';  -- 资源
UPDATE sys_menu SET visible = 0 WHERE code = 'safety:view';    -- 安全监管.view

-- ============================================================
-- 4. 修复图标（使用有效的 Ant Design Vue 图标）
-- ============================================================

-- 项目组织
UPDATE sys_menu SET icon = 'project' WHERE code = 'project_engineering';
UPDATE sys_menu SET icon = 'team' WHERE code = 'org';
UPDATE sys_menu SET icon = 'user' WHERE code = 'person';

-- 安全监管
UPDATE sys_menu SET icon = 'key' WHERE code = 'safety_access';
UPDATE sys_menu SET icon = 'qrcode' WHERE code = 'safety_code';
UPDATE sys_menu SET icon = 'idcard' WHERE code = 'safety_qualification';
UPDATE sys_menu SET icon = 'close-circle' WHERE code = 'safety_violation';
UPDATE sys_menu SET icon = 'check-square' WHERE code = 'safety_assessment';

-- 培训管理
UPDATE sys_menu SET icon = 'calendar' WHERE code = 'train_plan';
UPDATE sys_menu SET icon = 'project' WHERE code = 'train_project';
UPDATE sys_menu SET icon = 'video-camera' WHERE code = 'train_terminal';

-- 资源中心
UPDATE sys_menu SET icon = 'video-camera' WHERE code = 'resource_course';
UPDATE sys_menu SET icon = 'question-circle' WHERE code = 'resource_question';

-- 培训档案
UPDATE sys_menu SET icon = 'user' WHERE code = 'archives_user';
UPDATE sys_menu SET icon = 'folder' WHERE code = 'archives_training';

-- 运营中心
UPDATE sys_menu SET icon = 'bar-chart' WHERE code = 'operation_stats';
UPDATE sys_menu SET icon = 'notification' WHERE code = 'operation_news';
UPDATE sys_menu SET icon = 'bell' WHERE code = 'operation_notice';
UPDATE sys_menu SET icon = 'picture' WHERE code = 'operation_banner';

-- 系统管理
UPDATE sys_menu SET icon = 'user' WHERE code = 'sys_user';
UPDATE sys_menu SET icon = 'team' WHERE code = 'sys_role';
UPDATE sys_menu SET icon = 'menu' WHERE code = 'sys_menu';
UPDATE sys_menu SET icon = 'book' WHERE code = 'sys_dictionary';

COMMIT;
