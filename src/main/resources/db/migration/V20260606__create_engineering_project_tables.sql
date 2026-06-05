-- 工程项目相关表 V20260606
-- 创建 xm_engineering_project / xm_engineering_org_rel 两张表
-- 依据:com.joyfishs.dawa.project.entity.EngineeringProject / EngineeringOrgRel

CREATE TABLE IF NOT EXISTS `xm_engineering_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(200) DEFAULT NULL COMMENT '项目名称',
  `short_name` VARCHAR(100) DEFAULT NULL COMMENT '项目简称',
  `code` VARCHAR(64) DEFAULT NULL COMMENT '项目编码',
  `build_address` VARCHAR(500) DEFAULT NULL COMMENT '建设地点',
  `status` INT DEFAULT NULL COMMENT '状态：1=筹备中，2=进行中，3=已完成',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `is_delete` INT NOT NULL DEFAULT 0 COMMENT '是否删除：0=未删除，1=已删除',
  `delete_reason` VARCHAR(500) DEFAULT NULL COMMENT '删除原因',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  `delete_by` BIGINT DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`id`),
  KEY `idx_is_delete` (`is_delete`),
  KEY `idx_status` (`status`),
  KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程项目主表';

CREATE TABLE IF NOT EXISTS `xm_engineering_org_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `engineering_id` BIGINT DEFAULT NULL COMMENT '工程项目ID',
  `org_id` BIGINT DEFAULT NULL COMMENT '单位ID',
  `role_type` VARCHAR(32) DEFAULT NULL COMMENT '角色类型：owner=建设单位，design=设计单位，construct=施工单位，supervisor=监理单位',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_engineering_id` (`engineering_id`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程项目-单位关联表';
