-- 一人一档配置表添加工程项目关联字段
ALTER TABLE `person_archives_config` ADD COLUMN `engineering_id` bigint DEFAULT NULL COMMENT '关联工程项目 ID' AFTER `project_id`;
