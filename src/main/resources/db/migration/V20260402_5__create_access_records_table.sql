-- 创建 access_records 表
-- 执行时间：2026-04-02
-- 描述：创建准入记录表

CREATE TABLE IF NOT EXISTS `access_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` BIGINT COMMENT '关联用户 ID',
  `project_id` BIGINT COMMENT '关联项目 ID',
  `access_time` DATETIME COMMENT '进场时间',
  `exit_time` DATETIME COMMENT '出场时间',
  `access_type` VARCHAR(32) COMMENT '准入类型：normal/temporary/denied',
  `safety_code_color` VARCHAR(32) COMMENT '进场时安全码颜色',
  `gate_id` VARCHAR(64) COMMENT '闸机编号',
  `camera_snapshot` VARCHAR(512) COMMENT '抓拍照片 URL',
  `location` VARCHAR(256) COMMENT '位置信息',
  `remarks` VARCHAR(512) COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_access_type` (`access_type`),
  KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='准入记录表';
