-- 一人一档多档案配置系统 - 菜单数据
-- 执行时间：2026-04-02
-- 描述：添加一人一档多档案配置的菜单项

-- 说明：需要在 sys_menu 表中插入菜单数据
-- 菜单层级结构：
-- 数字档案 (一级菜单)
--   └── 岗位档案规范 (二级菜单) → 对应 JobList.vue
--         └── 档案配置管理 (三级菜单/页面) → 对应 PersonArchivesConfigList.vue
--               └── 档案数据管理 (页面) → 对应 PersonArchivesDataList.vue

-- 1. 插入一级菜单：数字档案 (如果不存在)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '数字档案', 'archives', 'folder', 0, 0, '/archives', 'RouteView', 1, 10, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives');

-- 2. 插入二级菜单：岗位档案规范 (如果不存在)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '岗位档案规范', 'archives_job', 'database',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives' LIMIT 1) AS tmp),
       0, '/archives/person/JobList', 'archives/person/JobList', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_job');

-- 3. 插入档案配置管理页面 (三级，作为 JobList 的子功能，不显示在菜单中)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '档案配置管理', 'archives_config', '',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives_job' LIMIT 1) AS tmp),
       1, '/archives/person/PersonArchivesConfigList', 'archives/person/PersonArchivesConfigList', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_config');

-- 4. 插入档案数据管理页面 (三级，作为 ConfigList 的子功能，不显示在菜单中)
INSERT INTO `sys_menu` (`name`, `code`, `icon`, `pid`, `type`, `router`, `component`, `visible`, `int_code`, `create_time`, `update_time`)
SELECT '档案数据管理', 'archives_data', '',
       (SELECT id FROM (SELECT id FROM `sys_menu` WHERE `code` = 'archives_config' LIMIT 1) AS tmp),
       1, '/archives/person/PersonArchivesDataList', 'archives/person/PersonArchivesDataList', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `code` = 'archives_data');

-- 备注：
-- type: 0=目录，1=菜单，2=按钮
-- visible: 1=显示，0=隐藏
-- 子功能页面设置为 hidden，通过程序内导航访问
