-- ============================================================
-- V20260613__create_safety_config.sql
-- 批次 D1: 新建 t_safety_config 配置表 + 种子数据
--
-- 目标: 把"颜色阈值/恢复分/超时天数/扣分上限"从代码硬编码
--       收敛到表里,业务方改配置不需要发版
--
-- 设计:
--   1) config_key 唯一,字符串编码 ("color.yellow.threshold")
--   2) config_group 用于前端分组展示 (color / score / retrain)
--   3) config_value 用 VARCHAR(255) - 数字也用字符串,避免 DECIMAL 类型困扰
--   4) value_type 提示前端做类型转换 (int / decimal / boolean / string)
--   5) is_editable=false 表示系统内置,前端不允许改
-- ============================================================

CREATE TABLE IF NOT EXISTS t_safety_config (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY                COMMENT '主键',
    config_key      VARCHAR(128) NOT NULL                            COMMENT '配置键 (如 color.yellow.threshold)',
    config_group    VARCHAR(64)  NOT NULL                            COMMENT '配置分组 color/score/retrain/role',
    config_value    VARCHAR(255) NOT NULL                            COMMENT '配置值 (字符串,前端按 value_type 转换)',
    value_type      VARCHAR(16)  NOT NULL DEFAULT 'string'           COMMENT '值类型 int/decimal/boolean/string/json',
    description     VARCHAR(255) NULL                                COMMENT '说明',
    is_editable     TINYINT(1)   NOT NULL DEFAULT 1                   COMMENT '是否允许前端修改 (0=系统内置)',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at      DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_safety_config_key (config_key),
    KEY idx_safety_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智理安全模块配置表';

-- ============================================================
-- 种子数据: 颜色阈值
-- 规则: score >= green.threshold → GREEN
--       score >= yellow.threshold → YELLOW
--       score <  yellow.threshold → RED
-- 之前硬编码: worker 6/12, manager 4/8
-- ============================================================
INSERT INTO t_safety_config (config_key, config_group, config_value, value_type, description, is_editable) VALUES
    ('color.worker.green.threshold',     'color',  '12',  'int', 'worker 工种: 积分 >= 此值 → 绿码', 0),
    ('color.worker.yellow.threshold',    'color',  '6',   'int', 'worker 工种: 积分 >= 此值且 < green → 黄码', 0),
    ('color.manager.green.threshold',    'color',  '8',   'int', 'manager 工种: 积分 >= 此值 → 绿码', 0),
    ('color.manager.yellow.threshold',   'color',  '4',   'int', 'manager 工种: 积分 >= 此值且 < green → 黄码', 0);

-- ============================================================
-- 种子数据: 积分恢复规则
-- 之前硬编码: 再培训完成后 +4 分
-- ============================================================
INSERT INTO t_safety_config (config_key, config_group, config_value, value_type, description, is_editable) VALUES
    ('score.retrain.restore',         'score',  '4',   'int',    '再培训完成后恢复积分', 0),
    ('score.initial',                 'score',  '12',  'int',    '新建账户初始积分', 0),
    ('score.deduct.max-per-violation','score',  '12',  'int',    '单次违章扣分上限', 0);

-- ============================================================
-- 种子数据: 再培训超时
-- 之前: 硬编码 7 天 (C3 已改为 trainingHours/8*7)
-- 现在: 配置 "天/周"系数, 公式不变
-- ============================================================
INSERT INTO t_safety_config (config_key, config_group, config_value, value_type, description, is_editable) VALUES
    ('retrain.overdue.days-per-week', 'retrain', '7',  'int',    '再培训超时阈值: trainingHours/8 * 此值 = 天数', 0),
    ('retrain.hours-per-day',         'retrain', '8',  'int',    '再培训学习强度: 每天学时', 0);

-- ============================================================
-- 种子数据: 角色白名单 (D2 依赖)
-- 之前: 硬编码在 edu-admin/src/utils/dawaRole.js
-- 现在: 后端配置,前端启动时 fetch
-- ============================================================
INSERT INTO t_safety_config (config_key, config_group, config_value, value_type, description, is_editable) VALUES
    ('role.admin.required-match',    'role',    'admin',                  'string', 'admin 角色必须包含的关键词', 0),
    ('role.admin.excluded-match',    'role',    'training,safety_supervisor', 'string', 'admin 角色必须排除的关键词 (逗号分隔)', 0);
