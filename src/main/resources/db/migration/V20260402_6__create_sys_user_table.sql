-- 创建 sys_user 表
-- 执行时间：2026-04-02
-- 描述：创建系统用户表

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户姓名',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `wx_open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信 OpenId',
  `sex` int DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盐 SALT',
  `status` int DEFAULT '0' COMMENT '状态 0-正常 1-禁用',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录 IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_count` int DEFAULT NULL COMMENT '登录次数',
  `id_card_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身份证号',
  `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人 ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志 1-已删除，0-未删除',
  `delete_by` bigint DEFAULT NULL COMMENT '删除人 ID',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '删除原因',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `current_safety_code_color` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '当前安全码颜色',
  `total_violation_score` int DEFAULT '0' COMMENT '累计违章记分',
  `last_violation_date` date DEFAULT NULL COMMENT '最后违章日期',
  `access_status` varchar(32) COLLATE utf8mb4_general_ci DEFAULT 'allowed' COMMENT '准入状态',
  `safety_code_expires_at` datetime DEFAULT NULL COMMENT '安全码过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `sys_user_phone_uindex` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- 插入测试用户 (password: admin123)
-- 使用 BCrypt 哈希：$2a$10$7JB7ZpQnFQWqQcJz.HVJwOKmVlOz7wZJZ5u5j5z5Z5u5j5z5Z5u5j
INSERT INTO `sys_user` (`name`, `user_name`, `phone`, `password`, `status`, `is_delete`)
VALUES ('管理员', 'admin', '13800138000', '$2a$10$7JB7ZpQnFQWqQcJz.HVJwOKmVlOz7wZJZ5u5j5z5Z5u5j5z5Z5u5j', 0, 0);
