-- 工程项目菜单
-- 添加到"培训项目"模块下（pid=4000）

-- 3.5 工程项目
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `int_code`, `pid`, `type`, `router`, `component`, `visible`, `create_time`, `is_delete`)
SELECT '工程项目', 'proj_engineering', 'appstore', 4005,
       4000,
       1, '/project/engineering/EngineeringProjectList', 'project/engineering/EngineeringProjectList', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'proj_engineering');

-- 获取刚插入的菜单ID
SET @parent_id = (SELECT id FROM sys_menu WHERE code = 'proj_engineering' LIMIT 1);

-- 工程项目相关权限
INSERT INTO `sys_menu` (`name`, `code`, `int_code`, `pid`, `type`, `permission`, `create_time`, `is_delete`)
SELECT 'engineering:add', 'engineering:add', 400501,
       @parent_id,
       2, 'engineering:add', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'engineering:add');

INSERT INTO `sys_menu` (`name`, `code`, `int_code`, `pid`, `type`, `permission`, `create_time`, `is_delete`)
SELECT 'engineering:edit', 'engineering:edit', 400502,
       @parent_id,
       2, 'engineering:edit', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'engineering:edit');

INSERT INTO `sys_menu` (`name`, `code`, `int_code`, `pid`, `type`, `permission`, `create_time`, `is_delete`)
SELECT 'engineering:del', 'engineering:del', 400503,
       @parent_id,
       2, 'engineering:del', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'engineering:del');
