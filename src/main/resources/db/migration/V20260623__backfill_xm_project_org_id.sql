-- V20260623: 回填 xm_project 表里 64 条 org_id IS NULL 的老项目
-- 现象: dev-sts 上 xm_project 共有 149 条 (is_delete=0),其中 64 条 org_id IS NULL
--   早期数据没有 org_id 字段,或者创建时没填,导致列表里看不到项目归属
-- 备份: /tmp/safe-edu-backups/xm_project_null_org_64_backup.sql (Mac 本地)
--
-- 分类规则 (关键词 -> org_id):
--   华夏水利 -> 77 (湖北华夏水利水电股份有限公司)
--   三峡     -> 20 (长江三峡技术经济发展有限公司)
--   海口/市政/其他/无关键词 -> 1 (子非鱼湖北信息科技有限公司,平台默认)
--
-- 设计: 多条 UPDATE 用 OR 串联,idempotent
--   再次跑这迁移时,所有 64 条 org_id 已经被填上,WHERE org_id IS NULL 不会命中,直接跳过

-- 1. 华夏水利 -> 77
UPDATE xm_project
   SET org_id = 77
 WHERE is_delete = 0
   AND org_id IS NULL
   AND (
     project_name LIKE '%华夏水利%'
   );

-- 2. 三峡 -> 20
--    例外: id=88 「三峡大学安全培训课程」指学校,不是长江三峡机构,走默认 (step 3)
UPDATE xm_project
   SET org_id = 20
 WHERE is_delete = 0
   AND org_id IS NULL
   AND id <> 88
   AND (
     project_name LIKE '%三峡%'
   );

-- 3. 其他 (海口/市政/测试/无关键词,以及 id=88 三峡大学) -> 1 (子非鱼默认)
--    排除 step 1/2 已经匹配过的关键词,避免被默认值覆盖
--    id=88 显式 OR 进来,因为它含「三峡」被 step 3 的 NOT LIKE 排除,但又被 step 2 排除
UPDATE xm_project
   SET org_id = 1
 WHERE is_delete = 0
   AND org_id IS NULL
   AND (
     id = 88
     OR (
       project_name NOT LIKE '%华夏水利%'
       AND project_name NOT LIKE '%三峡%'
     )
   );

-- 验证: 跑完后应该 0 条 NULL
SELECT
  COUNT(*) AS still_null,
  (SELECT COUNT(*) FROM xm_project WHERE is_delete=0) AS total_active
FROM xm_project
WHERE is_delete = 0 AND org_id IS NULL;
