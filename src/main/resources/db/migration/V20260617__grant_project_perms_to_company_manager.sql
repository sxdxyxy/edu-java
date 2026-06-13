-- =====================================================
-- V20260617: 给 company_manager 角色绑项目模块菜单
-- 创建时间: 2026-06-14
-- 说明: 修复 QA #23 — 公司管理员角色
--
--   company_manager (id=9) 之前无 sys_role_menu 关联, 无法访问项目模块.
--   现需绑:
--     1. 项目模块菜单树 (列表/编辑/发布)
--     2. 智理安全仪表盘 (兜底,V20260616 已加, 此处 IGNORE 保险)
--     3. 机构管理 (只读, 看自己公司)
--
-- 实施要点:
--   a) dev-sts 现状: sys_role 只有 platform_manager 一行
--      (见 V20260616 文件头说明).
--      company_manager 需要单独创建 — 本 migration 用 INSERT IGNORE
--      创建 (若已存在则跳过).
--   b) spec 列的 menu code 是冒号形式 ('project:add' 等),
--      但实际 dev-sts sys_menu.code 是下划线形式 ('project_list' 等).
--      此 migration 用 dev-sts 上**真实存在**的 code (见 V20260402_3
--      V20260525 V20260529),若 code 不存在 INSERT IGNORE 自动跳过,
--      不影响整体执行.
--   c) 全程 INSERT IGNORE,迁移可重复执行(Flyway 自身保证幂等).
-- =====================================================

-- 0. (可选) 创建 company_manager 角色
--    dev-sts 上若已存在则忽略;若不存在则创建 (id 由 MySQL 自增,
--    一般会落在 9 附近,但具体值不重要 — 所有后续查询都按 code 走).
INSERT IGNORE INTO sys_role (name, code, is_delete, create_time, update_time)
SELECT '公司管理员', 'company_manager', 0, NOW(), NOW()
 WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'company_manager');

-- 1. 项目模块菜单树
--    dev-sts 上真实存在的 code (按 V20260402_3 + V20260525 + V20260529 落地):
--      - project            (顶层,培训项目,V20260402_3)
--      - train_project      (培训项目,V20260525)
--      - train_plan         (培训计划,V20260525)
--      - train_terminal     (终端培训,V20260525)
--      - project_engineering(工程项目,V20260525 — 给 ProjectController 用)
--      - project_org        (项目组织父节点,V20260525)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
  FROM sys_role r
  JOIN sys_menu m
    ON m.code IN ('project', 'project_list', 'project_train_plan',
                  'project_class', 'project_terminal',
                  'train_project', 'train_plan', 'train_terminal',
                  'project_org', 'project_engineering')
   AND m.is_delete = 0
 WHERE r.code = 'company_manager';

-- 2. 智理安全仪表盘 (V20260616 兜底)
--    V20260616 已给所有非 admin 角色绑过;此处 IGNORE 保证重复执行不报错
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
  FROM sys_role r
  JOIN sys_menu m ON m.code = 'dawa_dashboard' AND m.is_delete = 0
 WHERE r.code = 'company_manager';

-- 3. 机构管理 (只读, 给看自己公司用)
--    dev-sts 上 org 是 V20260525 创建 (pid=project_org),
--    org:list / org:export 这两个 code 不存在 (无冒号形式权限 code),
--    INSERT IGNORE 自动跳过不存在的 code.
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
  FROM sys_role r
  JOIN sys_menu m
    ON m.code IN ('org', 'org:list', 'org:export')
   AND m.is_delete = 0
 WHERE r.code = 'company_manager';