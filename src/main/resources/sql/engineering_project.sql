-- 工程项目表
CREATE TABLE `xm_engineering_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) NOT NULL COMMENT '项目名称',
  `short_name` varchar(100) DEFAULT NULL COMMENT '项目简称',
  `code` varchar(50) DEFAULT NULL COMMENT '项目编码',
  `build_address` varchar(500) DEFAULT NULL COMMENT '建设地点',
  `status` tinyint DEFAULT '1' COMMENT '状态：1=筹备中，2=进行中，3=已完成',
  `remark` text COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `delete_by` bigint DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_reason` varchar(500) DEFAULT NULL COMMENT '删除原因',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程项目表';

-- 项目部表新增字段
ALTER TABLE `sys_org` ADD COLUMN `engineering_id` bigint DEFAULT NULL COMMENT '所属工程项目ID' AFTER `org_type`;
