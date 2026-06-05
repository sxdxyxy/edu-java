-- 菜单结构重构 V20260525
-- 重构系统菜单结构，按照项目组织、安全监管、培训管理等7个一级菜单组织
-- 执行前请备份数据！

START TRANSACTION;

-- ============================================================
-- 一级菜单：项目组织 (sort=10)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '项目组织', 'project_org', 'project', 0, 0, '/project-org', 'RouteView', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_org');

SET @pid_project_org = (SELECT `id` FROM `sys_menu` WHERE `code` = 'project_org' LIMIT 1);

-- 二级菜单：工程项目
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '工程项目', 'project_engineering', 'engineering', @pid_project_org, 0, '/project-org/engineering', 'project/EngineeringProject/index', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_engineering');

-- 二级菜单：项目部
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '项目部', 'project_dept', 'team', @pid_project_org, 0, '/project-org/dept', 'project/ProjectDept/index', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_dept');

-- 二级菜单：机构
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '机构', 'org', 'office', @pid_project_org, 0, '/project-org/org', 'system/org/index', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'org');

-- 二级菜单：人员
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '人员', 'person', 'people', @pid_project_org, 0, '/project-org/person', 'person/person/index', 1, 40, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'person');

-- ============================================================
-- 一级菜单：安全监管 (sort=20)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '安全监管', 'safety', 'safety-certificate', 0, 0, '/safety', 'RouteView', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety');

SET @pid_safety = (SELECT `id` FROM `sys_menu` WHERE `code` = 'safety' LIMIT 1);

-- 二级菜单：准入管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '准入管理', 'safety_access', 'SafetyAccess', @pid_safety, 0, '/safety/access', 'safety/AccessList', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety_access');

-- 二级菜单：安全码
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '安全码', 'safety_code', 'SafetyCode', @pid_safety, 0, '/safety/code', 'safety/SafetyCodeList', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety_code');

-- 二级菜单：资质
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '资质', 'safety_qualification', 'Qualification', @pid_safety, 0, '/safety/qualification', 'safety/QualificationList', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety_qualification');

-- 二级菜单：违章
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '违章', 'safety_violation', 'Violation', @pid_safety, 0, '/safety/violation', 'safety/ViolationList', 1, 40, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety_violation');

-- 二级菜单：考核
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '考核', 'safety_assessment', 'Assessment', @pid_safety, 0, '/safety/assessment', 'safety/AssessmentList', 1, 50, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety_assessment');

-- ============================================================
-- 一级菜单：培训管理 (sort=30)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '培训管理', 'training', 'book', 0, 0, '/training', 'RouteView', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'training');

SET @pid_training = (SELECT `id` FROM `sys_menu` WHERE `code` = 'training' LIMIT 1);

-- 二级菜单：培训计划
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '培训计划', 'train_plan', 'plan', @pid_training, 0, '/training/plan', 'training/TrainPlanList', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'train_plan');

-- 二级菜单：培训项目
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '培训项目', 'train_project', 'project', @pid_training, 0, '/training/project', 'training/TrainProjectList', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'train_project');

-- 二级菜单：终端培训
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '终端培训', 'train_terminal', 'terminal', @pid_training, 0, '/training/terminal', 'training/TerminalTrainList', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'train_terminal');

-- ============================================================
-- 一级菜单：资源中心 (sort=40)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '资源中心', 'resource', 'cloud-upload', 0, 0, '/resource', 'RouteView', 1, 40, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'resource');

SET @pid_resource = (SELECT `id` FROM `sys_menu` WHERE `code` = 'resource' LIMIT 1);

-- 二级菜单：课程
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '课程', 'resource_course', 'course', @pid_resource, 0, '/resource/course', 'resource/CourseList', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'resource_course');

-- 二级菜单：试题
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '试题', 'resource_question', 'question', @pid_resource, 0, '/resource/question', 'resource/QuestionList', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'resource_question');

-- 二级菜单：资源
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '资源', 'resource_file', 'file', @pid_resource, 0, '/resource/file', 'resource/FileList', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'resource_file');

-- ============================================================
-- 一级菜单：培训档案 (sort=50)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '培训档案', 'archives', 'folder', 0, 0, '/archives', 'RouteView', 1, 50, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives');

SET @pid_archives = (SELECT `id` FROM `sys_menu` WHERE `code` = 'archives' LIMIT 1);

-- 二级菜单：一人一档
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '一人一档', 'archives_user', 'user', @pid_archives, 0, '/archives/user', 'archives/PersonArchivesList', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_user');

-- 二级菜单：培训档案
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '培训档案', 'archives_training', 'training', @pid_archives, 0, '/archives/training', 'archives/TrainingArchivesList', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_training');

-- 二级菜单：岗位档案
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '岗位档案', 'archives_job', 'job', @pid_archives, 0, '/archives/job', 'archives/JobArchivesList', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_job');

-- ============================================================
-- 一级菜单：运营中心 (sort=60)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '运营中心', 'operation', 'bar-chart', 0, 0, '/operation', 'RouteView', 1, 60, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'operation');

SET @pid_operation = (SELECT `id` FROM `sys_menu` WHERE `code` = 'operation' LIMIT 1);

-- 二级菜单：数据统计
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '数据统计', 'operation_stats', 'statistics', @pid_operation, 0, '/operation/stats', 'operation/StatisticsDashboard', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'operation_stats');

-- 二级菜单：新闻管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '新闻管理', 'operation_news', 'news', @pid_operation, 0, '/operation/news', 'operation/NewsList', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'operation_news');

-- 二级菜单：公告管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '公告管理', 'operation_notice', 'notice', @pid_operation, 0, '/operation/notice', 'operation/NoticeList', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'operation_notice');

-- 二级菜单：Banner管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT 'Banner管理', 'operation_banner', 'banner', @pid_operation, 0, '/operation/banner', 'operation/BannerList', 1, 40, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'operation_banner');

-- ============================================================
-- 一级菜单：系统管理 (sort=70)
-- ============================================================
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '系统管理', 'system', 'setting', 0, 0, '/system', 'RouteView', 1, 70, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'system');

SET @pid_system = (SELECT `id` FROM `sys_menu` WHERE `code` = 'system' LIMIT 1);

-- ============================================================
-- 权限记录创建（按需执行）
-- ============================================================

-- 安全监管相关权限
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '安全监管.view', 'safety:view', '', @pid_safety, 1, '', '', 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'safety:view' AND `type` = 1);

COMMIT;
