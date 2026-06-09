-- =====================================================
-- V20260609: 清理旧菜单架构,只保留新 7 主菜单架构
-- 创建时间: 2026-06-09
-- 修订: 2026-06-09 22:xx
--   - 补孤立老根清理(pid=0, pids='[0],' 且 id != 300)
--   - 补孤儿按钮清理(100/168/169/170/223 及 99/136/164/165 后代)
--   - 把 3 个新菜单 component 改写改成基于 code 匹配,这样本地 safe_train 和 dev-sts safe_train_v3 都能命中
--
-- 说明:
--   当前 sys_menu 同时存在两套菜单架构:
--     - 老根(pid=0, pids='[0],') 多个,除 300 我的保留外,其他全部软删
--     - 新根(pid=0, pids IS NULL) 8 个(其中 308 visible=0):
--       项目组织 / 培训管理 / 资源中心 / 培训档案 /
--       运营中心 / 系统管理 / 智理安全
--   平台管理员(role 2) sys_role_menu 同时持有了两套架构的权限,
--   导致侧边栏显示 16+ 个菜单,且老根依旧排在前面。
--
--   此次清理:
--     1. 软删老根 2/11/14/23/29/147/233/247 及它们的所有后代
--     2. 软删孤立老根(pid=0, pids='[0],' 且 id != 300)
--     3. 软删 5 个孤儿按钮及挂在孤立老根(99/136/164/165)下的所有后代
--     4. 保留 300 我的 及后代(给学员端移动端用)
--     5. role 2 (平台管理员) 移除所有被软删菜单的 sys_role_menu 记录
--     6. role 3 (学员) 不动
--     7. 把 3 个新菜单的 component 改成真实存在的 Vue 文件(用 code 匹配)
--
-- 兼容: MySQL 8 (Flyway 自动管理)
-- 幂等: 所有 UPDATE 都有 is_delete=0 守卫,可重复执行
-- =====================================================

-- 1. 软删老根(2/11/14/23/29/147/233/247)及它们的所有后代
UPDATE sys_menu
SET is_delete = 1,
    delete_by = 1,
    delete_time = NOW(),
    delete_reason = 'V20260609 老菜单清理(保留 300 我的)'
WHERE is_delete = 0
  AND (
    (pid = 0 AND id IN (2, 11, 14, 23, 29, 147, 233, 247))
    OR
    pids REGEXP '^\\[0\\],\\[(2|11|14|23|29|147|233|247)\\]'
  );

-- 2. 软删孤立老根(pid=0, pids='[0],',排除 300 我的)
--    涵盖: 99/130/131/136/141/143/144/145/158/160/164/165/166/167/172/188/211/240 等
UPDATE sys_menu
SET is_delete = 1,
    delete_by = 1,
    delete_time = NOW(),
    delete_reason = 'V20260609 老菜单清理-孤立老根收尾'
WHERE is_delete = 0
  AND pid = 0
  AND pids = '[0],'
  AND id != 300;

-- 3. 软删 5 个孤儿按钮及挂在孤立老根(99/136/164/165)下的所有后代
UPDATE sys_menu
SET is_delete = 1,
    delete_by = 1,
    delete_time = NOW(),
    delete_reason = 'V20260609 老菜单清理-孤儿收尾(保留 300 我的)'
WHERE is_delete = 0
  AND (
    id IN (100, 168, 169, 170, 223)
    OR
    pids REGEXP '^\\[0\\],\\[(99|136|164|165)\\]'
  );

-- 4. 清掉 role 2 (平台管理员) 的所有被软删菜单权限
--    role 3 (学员) 不动
DELETE FROM sys_role_menu
WHERE role_id = 2
  AND menu_id IN (
    SELECT id FROM sys_menu
    WHERE is_delete = 1
      AND delete_reason LIKE 'V20260609%'
  );

-- 5. 把 3 个新菜单 component 改成真实存在的 Vue 文件
--    用 code 匹配而不是 id,这样本地 safe_train(305) 和 dev-sts safe_train_v3(265) 都能命中
--    幂等:只改不在目标 component 状态的菜单
UPDATE sys_menu SET component = 'org/OrgList'
WHERE code = 'project_dept' AND (component = 'project/ProjectDept/index' OR component = '' OR component IS NULL);

UPDATE sys_menu SET component = 'course/Resource'
WHERE code = 'resource_file' AND (component = 'resource/FileList' OR component = '' OR component IS NULL);

UPDATE sys_menu SET component = 'archives/person/JobList'
WHERE code = 'archives_job' AND (component = 'archives/JobArchivesList' OR component = '' OR component IS NULL);
