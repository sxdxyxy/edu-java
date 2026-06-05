-- 创建 sys_role 和 sys_user_role 表
-- 执行时间：2026-04-02
-- 描述：创建角色表和用户角色关联表

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色编码',
  `sort` int DEFAULT NULL COMMENT '排序',
  `org_code` bigint DEFAULT NULL COMMENT '所属组织编码',
  `parent_id` bigint DEFAULT '-1' COMMENT '父角色 ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志 1-已删除，0-未删除',
  `delete_by` bigint DEFAULT NULL COMMENT '删除人 ID',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '删除原因',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户 ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- 插入平台管理员角色
INSERT INTO `sys_role` (`name`, `code`, `is_delete`) VALUES ('平台管理员', 'platform_manager', 0);

-- 关联 admin 用户到平台管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT
  (SELECT id FROM sys_user WHERE user_name = 'admin' LIMIT 1),
  (SELECT id FROM sys_role WHERE code = 'platform_manager' LIMIT 1);
