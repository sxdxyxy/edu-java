-- 安全监管与培训系统联动 - 特种作业类型初始数据
-- 执行时间：2026-05-26
-- 描述：插入特种作业类型初始数据

INSERT INTO t_special_work_type (work_type_code, work_type_name, danger_level, default_score, certificate_valid_years, status, remarks) VALUES
-- 电工作业
('ELEC_HIGH', '电工作业（高压）', 'high', 15, 6, 'enabled', '从事高压电网设备操作'),
('ELEC_LOW', '电工作业（低压）', 'high', 15, 6, 'enabled', '从事低压电气设备操作'),
('ELEC_TEST', '电气试验', 'high', 15, 6, 'enabled', '电气设备试验、检测'),
('ELEC_RELAY', '继电保护', 'high', 15, 6, 'enabled', '继电保护装置操作'),
('ELEC_CABLE', '电力电缆', 'high', 15, 6, 'enabled', '电力电缆安装、维护'),

-- 焊接与热切割
('WELD_CUT', '焊接与热切割', 'high', 15, 6, 'enabled', '焊接、切割、钎焊作业'),

-- 高处作业
('HEIGHT_BUILD', '高处作业（登高架设）', 'high', 15, 6, 'enabled', '登高架设作业'),
('HEIGHT_INSTALL', '高处作业（安装维护拆除）', 'high', 15, 6, 'enabled', '高处安装、维护、拆除'),

-- 制冷与空调
('REFRIGERATION', '制冷与空调设备操作', 'medium', 15, 6, 'enabled', '制冷空调设备操作'),

-- 煤矿安全
('COAL_MINE', '煤矿井下安全作业', 'high', 15, 6, 'enabled', '煤矿井下作业'),

-- 金属非金属矿山
('METAL_MINE', '金属非金属矿山安全作业', 'high', 15, 6, 'enabled', '矿山井下/露天作业'),

-- 石油天然气
('OIL_GAS', '石油天然气安全作业', 'high', 15, 6, 'enabled', '油气勘探、开采、运输'),

-- 冶金（有色）
('METALLURGY', '冶金（有色）安全作业', 'high', 15, 6, 'enabled', '煤气、冶金作业'),

-- 危险化学品
('DANGEROUS_CHEM', '危险化学品安全作业', 'high', 15, 6, 'enabled', '化工操作、危化品运输'),

-- 烟花爆竹
('FIREWORK', '烟花爆竹安全作业', 'high', 15, 6, 'enabled', '烟火药制造、黑火药制造'),

-- 安全监管总局认定
('OTHER', '安全监管总局认定作业', 'medium', 15, 6, 'enabled', '其他特种作业');
