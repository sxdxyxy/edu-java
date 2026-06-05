-- 菜单结构重构 - 完整菜单数据
-- 执行时间：2026-04-02
-- 描述：重新组织所有菜单项，按照管理流程逻辑

-- 菜单结构说明：
-- 1. 智理安全 (dawa) - 安全监管核心功能
-- 2. 培训资源 (course) - 课程、试题、资源管理
-- 3. 培训项目 (project) - 项目、培训计划、班级管理
-- 4. 人员机构 (person/org) - 人员和机构管理
-- 5. 培训档案 (archives) - 一人一档和档案管理
-- 6. 数据统计 (data) - 各类统计数据
-- 7. 新闻公告 (app) - 新闻、公告、必读管理
-- 8. 系统管理 (sys) - 用户、角色、菜单、字典等

-- ============================================
-- 一级菜单
-- ============================================

-- 1. 智理安全 (pid=0, sort=10)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '智理安全', 'dawa', 'safety-certificate', 0, 0, '/dawa', 'RouteView', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa');

-- 2. 培训资源 (pid=0, sort=20)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '培训资源', 'course', 'book', 0, 0, '/course', 'RouteView', 1, 20, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'course');

-- 3. 培训项目 (pid=0, sort=30)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '培训项目', 'project', 'project', 0, 0, '/project', 'RouteView', 1, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project');

-- 4. 人员机构 (pid=0, sort=40)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '人员机构', 'person_org', 'team', 0, 0, '/person', 'RouteView', 1, 40, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'person_org');

-- 5. 培训档案 (pid=0, sort=50)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '培训档案', 'archives', 'folder', 0, 0, '/archives', 'RouteView', 1, 50, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives');

-- 6. 数据统计 (pid=0, sort=60)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '数据统计', 'data', 'bar-chart', 0, 0, '/data', 'RouteView', 1, 60, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'data');

-- 7. 新闻公告 (pid=0, sort=70)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '新闻公告', 'app_news', 'notification', 0, 0, '/app', 'RouteView', 1, 70, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'app_news');

-- 8. 系统管理 (pid=0, sort=80)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '系统管理', 'system', 'setting', 0, 0, '/system', 'RouteView', 1, 80, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'system');

-- ============================================
-- 智理安全 子菜单 (pid=dawa)
-- ============================================

-- 1.1 安全仪表盘
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '安全仪表盘', 'dawa_dashboard', 'dashboard',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/dashboard', 'dawa/SafetyDashboard', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_dashboard');

-- 1.2 准入管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '准入管理', 'dawa_access', 'login',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/access-list', 'dawa/AccessList', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_access');

-- 1.3 安全码管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '安全码管理', 'dawa_safety_code', 'qrcode',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/safety-code-list', 'dawa/SafetyCodeList', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_safety_code');

-- 1.4 资质管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '资质管理', 'dawa_qualification', 'certificate',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/qualification-list', 'dawa/QualificationList', 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_qualification');

-- 1.5 违章管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '违章管理', 'dawa_violation', 'exclamation-circle',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/violation-list', 'dawa/ViolationList', 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_violation');

-- 1.6 考核管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '考核管理', 'dawa_assessment', 'file-text',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'dawa' LIMIT 1) AS tmp),
       1, '/dawa/assessment-list', 'dawa/AssessmentList', 1, 6, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'dawa_assessment');

-- ============================================
-- 培训资源 子菜单 (pid=course)
-- ============================================

-- 2.1 课程管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '课程管理', 'course_list', 'book',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'course' LIMIT 1) AS tmp),
       1, '/course/CourseList', 'course/CourseList', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'course_list');

-- 2.2 试题管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '试题管理', 'course_question', 'edit',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'course' LIMIT 1) AS tmp),
       1, '/course/question/QuestionList', 'course/question/QuestionList', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'course_question');

-- 2.3 资源管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '资源管理', 'course_resource', 'cloud-upload',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'course' LIMIT 1) AS tmp),
       1, '/course/Resource', 'course/Resource', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'course_resource');

-- ============================================
-- 培训项目 子菜单 (pid=project)
-- ============================================

-- 3.1 项目列表
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '项目列表', 'project_list', 'project',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'project' LIMIT 1) AS tmp),
       1, '/project/ProjectList', 'project/ProjectList', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_list');

-- 3.2 培训计划
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '培训计划', 'project_train_plan', 'calendar',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'project' LIMIT 1) AS tmp),
       1, '/project/trainPlan/TrainPlanList', 'project/trainPlan/TrainPlanList', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_train_plan');

-- 3.3 班级列表
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '班级列表', 'project_class', 'team',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'project' LIMIT 1) AS tmp),
       1, '/project/classAdd/ClassList', 'project/classAdd/ClassList', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_class');

-- 3.4 终端培训
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '终端培训', 'project_terminal', 'laptop',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'project' LIMIT 1) AS tmp),
       1, '/project/terminalTrain/TerminalTrainList', 'project/terminalTrain/TerminalTrainList', 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'project_terminal');

-- ============================================
-- 人员机构 子菜单 (pid=person_org)
-- ============================================

-- 4.1 人员列表
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '人员列表', 'person_list', 'user',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'person_org' LIMIT 1) AS tmp),
       1, '/person/PersonList', 'person/PersonList', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'person_list');

-- 4.2 机构列表
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '机构列表', 'org_list', 'apartment',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'person_org' LIMIT 1) AS tmp),
       1, '/org/OrgList', 'org/OrgList', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'org_list');

-- ============================================
-- 培训档案 子菜单 (pid=archives)
-- ============================================

-- 5.1 一人一档 (学员档案)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '一人一档', 'archives_user', 'idcard',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives' LIMIT 1) AS tmp),
       1, '/archives/user/Index', 'archives/user/Index', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_user');

-- 5.2 培训档案
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '培训档案', 'archives_project', 'file-text',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives' LIMIT 1) AS tmp),
       1, '/archives/project/Index', 'archives/project/Index', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_project');

-- 5.3 岗位档案规范
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '岗位档案规范', 'archives_job', 'database',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives' LIMIT 1) AS tmp),
       1, '/archives/person/JobList', 'archives/person/JobList', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_job');

-- 5.4 档案配置管理 (隐藏，从岗位档案规范进入)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '档案配置管理', 'archives_config', '',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives_job' LIMIT 1) AS tmp),
       1, '/archives/person/PersonArchivesConfigList', 'archives/person/PersonArchivesConfigList', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_config');

-- 5.5 档案数据管理 (隐藏，从档案配置管理进入)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '档案数据管理', 'archives_data', '',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives_config' LIMIT 1) AS tmp),
       1, '/archives/person/PersonArchivesDataList', 'archives/person/PersonArchivesDataList', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_data');

-- ============================================
-- 数据统计 子菜单 (pid=data)
-- ============================================

-- 6.1 数据统计
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '数据统计', 'data_statistic', 'bar-chart',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'data' LIMIT 1) AS tmp),
       1, '/data/DataStatistic', 'data/DataStatistic', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'data_statistic');

-- 6.2 证书统计
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '证书统计', 'data_certificate', 'trophy',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'data' LIMIT 1) AS tmp),
       1, '/data/Certificate', 'data/Certificate', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'data_certificate');

-- 6.3 积分排行
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '积分排行', 'data_score_rank', 'star',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'data' LIMIT 1) AS tmp),
       1, '/data/ScoreRank', 'data/ScoreRank', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'data_score_rank');

-- ============================================
-- 新闻公告 子菜单 (pid=app_news)
-- ============================================

-- 7.1 新闻管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '新闻管理', 'app_news_list', 'file-text',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'app_news' LIMIT 1) AS tmp),
       1, '/appNews/list', 'appNews/list', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'app_news_list');

-- 7.2 公告管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '公告管理', 'app_notice_list', 'notification',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'app_news' LIMIT 1) AS tmp),
       1, '/appNotice/list', 'appNotice/list', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'app_notice_list');

-- 7.3 必读管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '必读管理', 'app_must_read', 'book',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'app_news' LIMIT 1) AS tmp),
       1, '/appMustRead/list', 'appMustRead/list', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'app_must_read');

-- 7.4 Banner 管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT 'Banner 管理', 'app_banner', 'picture',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'app_news' LIMIT 1) AS tmp),
       1, '/banner/bannerList', 'banner/bannerList', 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'app_banner');

-- ============================================
-- 系统管理 子菜单 (pid=system)
-- ============================================

-- 8.1 用户管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '用户管理', 'sys_user', 'user',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'system' LIMIT 1) AS tmp),
       1, '/security/user/UserList', 'security/user/UserList', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'sys_user');

-- 8.2 角色管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '角色管理', 'sys_role', 'team',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'system' LIMIT 1) AS tmp),
       1, '/security/role/RoleList', 'security/role/RoleList', 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'sys_role');

-- 8.3 菜单管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '菜单管理', 'sys_menu', 'menu',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'system' LIMIT 1) AS tmp),
       1, '/security/menu/MenuList', 'security/menu/MenuList', 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'sys_menu');

-- 8.4 字典管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '字典管理', 'sys_dict', 'dictionary',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'system' LIMIT 1) AS tmp),
       1, '/sys/dictionary/DictionaryList', 'sys/dictionary/DictionaryList', 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'sys_dict');

-- 8.5 短信管理
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `sort_no`, `created_at`, `updated_at`)
SELECT '短信管理', 'sys_sms', 'message',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'system' LIMIT 1) AS tmp),
       1, '/sms/smsList', 'sms/smsList', 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'sys_sms');

-- ============================================
-- 更新现有菜单的 pid
-- ============================================
-- 注意：如果已有菜单数据，需要更新 pid 以匹配新的层级结构
-- 这将在应用此迁移后由后端处理

-- 备注：
-- type: 0=目录 (一级菜单), 1=菜单 (二级菜单/页面), 2=按钮
-- visible: 1=显示，0=隐藏 (内部页面)
-- sort_no: 排序号，数字越小越靠前
