-- 人员档案配置表
DROP TABLE IF EXISTS `person_archives_config`;
CREATE TABLE `person_archives_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_type` int NOT NULL COMMENT '工种类型 (1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工)',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '档案配置名称 (如：入职档案、培训档案、考核档案)',
  `config_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置编码 (如：ENTRY, TRAINING, ASSESSMENT)',
  `org_id` bigint NOT NULL COMMENT '机构 ID',
  `template_fields` json DEFAULT NULL COMMENT '档案字段配置 (JSON 格式定义包含的字段)',
  `document_template_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文档模板 URL',
  `document_template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文档模板名称',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_work_type` (`work_type`),
  KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='人员档案配置表';

-- 人员档案数据表
DROP TABLE IF EXISTS `person_archives_data`;
CREATE TABLE `person_archives_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_id` bigint NOT NULL COMMENT '关联配置 ID',
  `person_id` bigint NOT NULL COMMENT '人员 ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户 ID',
  `work_type` int NOT NULL COMMENT '工种类型',
  `org_id` bigint NOT NULL COMMENT '机构 ID',
  `project_id` bigint DEFAULT NULL COMMENT '项目 ID',
  `team_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '班组名称',
  `archive_data` json DEFAULT NULL COMMENT '档案数据内容 (JSON 格式)',
  `generated_doc_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成的文档 URL',
  `archive_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '档案编号',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '状态 (draft/pending/approved/archived)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_work_type` (`work_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='人员档案数据表';
