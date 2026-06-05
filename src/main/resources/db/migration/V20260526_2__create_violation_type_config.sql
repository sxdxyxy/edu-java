-- 安全监管与培训系统联动 - 违章类型配置表
-- 执行时间：2026-05-26
-- 描述：创建违章类型配置表，定义违章扣分规则和触发培训条件

CREATE TABLE IF NOT EXISTS `t_violation_type_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `violation_code` VARCHAR(50) NOT NULL COMMENT '违章代码',
    `violation_name` VARCHAR(100) NOT NULL COMMENT '违章名称',
    `violation_level` VARCHAR(20) NOT NULL COMMENT '级别 minor/major/critical',
    `deduct_score` INT NOT NULL COMMENT '扣分分值',
    `trigger_training` TINYINT DEFAULT 1 COMMENT '是否触发强制培训 0否 1是',
    `training_hours` INT DEFAULT 0 COMMENT '触发培训学时',
    `status` VARCHAR(20) DEFAULT 'enabled' COMMENT '状态 enabled/disabled',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_violation_code` (`violation_code`),
    KEY `idx_status` (`status`),
    KEY `idx_violation_level` (`violation_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违章类型配置表';

-- 初始数据：常见违章类型
INSERT INTO t_violation_type_config (violation_code, violation_name, violation_level, deduct_score, trigger_training, training_hours, sort_order) VALUES
('NO_HELMET', '未佩戴安全帽', 'minor', 2, 1, 2, 1),
('NO_SAFETY_BELT', '未佩戴安全带', 'minor', 2, 1, 2, 2),
('TRAINING_ABSENT', '培训缺席', 'minor', 2, 1, 2, 3),
('NO_PROTECTIVE_EQUIPMENT', '未佩戴防护用品', 'minor', 2, 1, 2, 4),
('SMOKING_IN_NO_SMOKING_AREA', '在禁烟区吸烟', 'minor', 2, 1, 2, 5),
('NO_CERT', '未持证上岗', 'major', 6, 1, 8, 10),
('VIOLATION_OPERATION', '违规操作', 'major', 6, 1, 8, 11),
('UNAUTHORIZED_ENTRY', '未经授权进入危险区域', 'major', 6, 1, 8, 12),
('HIDING_ACCIDENT', '隐瞒事故隐患', 'major', 6, 1, 8, 13),
('REFUSING_SAFETY_INSPECTION', '拒绝安全检查', 'major', 4, 1, 4, 14),
('SAFETY_ACCIDENT', '发生安全事故', 'critical', 12, 1, 16, 20),
('INTENTIONAL_VIOLATION', '故意违章', 'critical', 12, 1, 16, 21),
('DESTROYING_SAFETY_FACILITIES', '故意破坏安全设施', 'critical', 12, 1, 16, 22),
('DRUNK_WORK', '酒后作业', 'critical', 12, 1, 16, 23);
