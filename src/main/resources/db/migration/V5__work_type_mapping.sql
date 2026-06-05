-- =====================================================
-- V5: 工种与岗位类型映射表
-- 创建时间: 2026-05-31
-- 说明: 安全积分账户岗位类型与培训系统工种(WorkType 1-19)建立映射
--       违章扣分标准统一，不分工种
-- =====================================================

-- 工种岗位映射表
CREATE TABLE IF NOT EXISTS t_work_type_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    account_work_type VARCHAR(32) NOT NULL COMMENT '安全积分岗位代码：worker-普通工人，specialized-特种工种，safety_admin-安全管理人员',
    work_type INT NOT NULL COMMENT '培训系统工种编码（1-19），对应 xm_person.work_type',
    work_type_name VARCHAR(64) NOT NULL COMMENT '工种名称',
    initial_score INT DEFAULT 12 COMMENT '初始积分（统一12分）',
    green_threshold INT DEFAULT 12 COMMENT '绿码阈值',
    yellow_threshold INT DEFAULT 6 COMMENT '黄码阈值',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_work_type (work_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工种岗位映射表';

-- 初始化数据：所有工种统一 worker 岗位类型，初始12分
-- 工种编码对应：1=普工，2=电工，3=焊工，4=钢筋工，5=混凝土，6=架子工
--              7=管理人员，8=木工，9=瓦工，10=顶管工，11=叉车工
--              12=登高车操作工，13=驾驶员，14=汽车吊司机，15=挖机操作工
--              16=信号司索工，17=压路机，18=桩机操作手，19=装载机操作工
INSERT INTO t_work_type_mapping (account_work_type, work_type, work_type_name, initial_score, green_threshold, yellow_threshold) VALUES
('worker', 1, '普工',          12, 12, 6),
('worker', 4, '钢筋工',        12, 12, 6),
('worker', 5, '混凝土',        12, 12, 6),
('worker', 8, '木工',          12, 12, 6),
('worker', 9, '瓦工',          12, 12, 6),
('worker', 10, '顶管工',       12, 12, 6),
('worker', 11, '叉车工',       12, 12, 6),
('worker', 12, '登高车操作工', 12, 12, 6),
('worker', 13, '驾驶员',       12, 12, 6),
('worker', 14, '汽车吊司机',   12, 12, 6),
('worker', 15, '挖机操作工',   12, 12, 6),
('worker', 16, '信号司索工',   12, 12, 6),
('worker', 17, '压路机',       12, 12, 6),
('worker', 18, '桩机操作手',   12, 12, 6),
('worker', 19, '装载机操作工',  12, 12, 6),
('specialized', 2, '电工',     12, 12, 6),
('specialized', 3, '焊工',     12, 12, 6),
('specialized', 6, '架子工',   12, 12, 6),
('safety_admin', 7, '管理人员', 10, 10, 5);

-- =====================================================
-- 完成
-- =====================================================

-- 为 t_safety_score_account 添加人员工种字段
ALTER TABLE t_safety_score_account
ADD COLUMN IF NOT EXISTS person_work_type INT COMMENT '人员工种编码（xm_person.work_type，1-19）';
