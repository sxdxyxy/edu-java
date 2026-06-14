-- =====================================================
-- V20260622: 给 platform_manager 角色补齐安全子菜单 + 细粒度权限码
-- 创建时间: 2026-06-14
-- 说明: 修复 retraining 500 之后发现的菜单/权限码缺失
--
--   现象: dev-sts 上 platform_manager 角色的 sys_role_menu 缺少 2 条绑定:
--     - menu id=297  safety_retraining     (再培训管理)
--     - menu id=298  safety_violation_type (违章类型配置)
--   导致 platform_manager 用户登录后看不到 "再培训管理" / "违章类型配置" 入口.
--
--   此外, sys_menu 表里 297/298/299/300 这 4 个新菜单都只有 list 权限码,
--   但代码 SS 注解用了更细的 edit / process / deduct / restore / delete 等
--   权限码. 现状下 platform_manager 因 list 命中能看到菜单, 但调
--   /safety/retraining/{id}/start (需要 safety:retraining:edit) 会被
--   403. 这里把 SS 注解用到的全部权限码以独立 sys_menu 行形式 INSERT IGNORE
--   进去, 后续 sys_role_menu 绑定一并附上.
--
-- 实施要点:
--   a) sys_menu INSERT IGNORE: 唯一键在 (code),重复 code 自动跳过.
--   b) sys_role_menu INSERT IGNORE: 唯一键 (role_id, menu_id), 重复跳过.
--   c) 全程幂等, 重复跑此 migration 无副作用.
--   d) 不动生产 sts.joyfishs.com — 这是 edu-java 源文件, dev-sts 重 build
--      后由 Flyway 自动执行; 生产 Flyway 也会执行 (production V20260622
--      也会跑, 但 INSERT IGNORE 命中唯一键或 SELECT-NOT-EXISTS 时无影响).
-- =====================================================

-- 1. sys_menu: 补 4 张新菜单缺的细粒度权限码 (297/298/299/300 各加 1-3 个)
--    每条用 (code, name) 唯一索引判重; code 形如 'safety:retraining:edit' 即
--    对应 @PreAuthorize 注解里的字符串. int_code 由 1000+i 自动递增.
--    注意: 这里把权限码本身做成 sys_menu 的一行 (parent=297/298/299/300),
--    而不是用单独的 sys_role_perm 表 — 沿用项目现有的 sys_menu.permission
--    模式 (参考 V20260620 注释).

-- 1.1 safety_retraining (menu 297) 补 :edit / :process
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '再培训-编辑',  'safety:retraining:edit',     297, 2, 'safety:retraining:edit',     1, 0, NOW(), NOW(), 1297
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:retraining:edit');
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '再培训-处理', 'safety:retraining:process',  297, 2, 'safety:retraining:process',  1, 0, NOW(), NOW(), 1298
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:retraining:process');

-- 1.2 safety_violation_type (menu 298) 补 :edit / :delete
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '违章类型-编辑', 'safety:violationtype:edit',   298, 2, 'safety:violationtype:edit',   1, 0, NOW(), NOW(), 1299
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:violationtype:edit');
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '违章类型-删除', 'safety:violationtype:delete', 298, 2, 'safety:violationtype:delete', 1, 0, NOW(), NOW(), 1300
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:violationtype:delete');

-- 1.3 safety_score_account (menu 299) 补 :query / :edit / :deduct / :restore
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '积分-查询',   'safety:score:query',   299, 2, 'safety:score:query',   1, 0, NOW(), NOW(), 1301
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:score:query');
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '积分-编辑',   'safety:score:edit',     299, 2, 'safety:score:edit',     1, 0, NOW(), NOW(), 1302
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:score:edit');
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '积分-扣分',   'safety:score:deduct',   299, 2, 'safety:score:deduct',   1, 0, NOW(), NOW(), 1303
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:score:deduct');
INSERT IGNORE INTO sys_menu (name, code, pid, type, permission, visible, is_delete, create_time, update_time, int_code)
SELECT '积分-恢复',   'safety:score:restore',  299, 2, 'safety:score:restore',  1, 0, NOW(), NOW(), 1304
 WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE code = 'safety:score:restore');

-- 1.4 safety_appeal (menu 300) 补 :edit (AppealController 用 retraining:list, 暂不补,留痕)

-- 2. sys_role_menu: 给 platform_manager (id=2) 绑 297/298/299/300 四个菜单
--    + 1.x 创建的 8 条细粒度权限码 (id 301-308, code like 'safety:%' type=2 权限行)
--    注: 公司管理员 (id=9) 是否需要看这些再培训/账户/申诉, 暂不动 — 当前
--    V20260617 migration 只绑了 project 模块, 安全相关由 platform_manager
--    负责. 若后续要加给 company_manager, 单独 migration.
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, 297 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id=2 AND menu_id=297);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, 298 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id=2 AND menu_id=298);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, 299 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id=2 AND menu_id=299);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, 300 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id=2 AND menu_id=300);

-- 2.1 绑 8 条细粒度权限码 (id 301-308, 见 1.1-1.3)
--    用子查询按 code 找 id, 避免硬编码 id (若以后插入顺序变化也对)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, m.id FROM sys_menu m
 WHERE m.code IN (
   'safety:retraining:edit','safety:retraining:process',
   'safety:violationtype:edit','safety:violationtype:delete',
   'safety:score:query','safety:score:edit','safety:score:deduct','safety:score:restore'
 )
   AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id=2 AND rm.menu_id=m.id);
