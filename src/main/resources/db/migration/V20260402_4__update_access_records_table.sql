-- 更新 access_records 表结构
-- 执行时间：2026-04-02
-- 描述：添加 project_id、exit_time、updated_at 字段，扩展 location 和 remarks 字段

-- 添加 project_id 字段
ALTER TABLE `access_records`
ADD COLUMN `project_id` BIGINT COMMENT '关联项目 ID' AFTER `user_id`;

-- 添加 exit_time 字段
ALTER TABLE `access_records`
ADD COLUMN `exit_time` DATETIME COMMENT '出场时间' AFTER `access_time`;

-- 添加 updated_at 字段
ALTER TABLE `access_records`
ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `created_at`;

-- 修改 location 字段（如果已存在则扩大长度）
ALTER TABLE `access_records`
MODIFY COLUMN `location` VARCHAR(256) COMMENT '位置信息';

-- 添加 remarks 字段
ALTER TABLE `access_records`
ADD COLUMN `remarks` VARCHAR(512) COMMENT '备注' AFTER `location`;

-- 添加索引
ALTER TABLE `access_records`
ADD KEY `idx_project_id` (`project_id`),
ADD KEY `idx_access_type` (`access_type`);
