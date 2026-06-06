-- =====================================================
-- V20260606_3: violation_records 加乐观锁版本号
-- 创建时间: 2026-06-06
-- 说明: 同一人员多条违章同时录入时,扣分更新可能并发覆盖。
--       加 version 列配合 MyBatis Plus @Version 注解,
--       后到的 update 会失败抛 OptimisticLockingFailureException。
-- 兼容: MySQL 8 不支持 ADD COLUMN IF NOT EXISTS,
--       用 session 变量 + PREPARE 实现幂等
-- =====================================================

-- 幂等添加 version 列 (MySQL 8 兼容写法)
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'violation_records'
      AND column_name = 'version'
);

SET @ddl := IF(
    @col_exists = 0,
    'ALTER TABLE violation_records ADD COLUMN version INT DEFAULT 0 COMMENT ''乐观锁版本号(由 MyBatis Plus @Version 自动管理)''',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 老数据初始化为 0 (如果列刚加,默认就是 0;这段是安全网)
UPDATE violation_records SET version = 0 WHERE version IS NULL;
