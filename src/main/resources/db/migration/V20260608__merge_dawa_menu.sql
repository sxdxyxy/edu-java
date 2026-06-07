-- =====================================================
-- V20260608: 合并"我的工作台"到"安全仪表盘"
-- 创建时间: 2026-06-07
-- 说明: V20260607 临时新建了 dawa_dashboard 与 safety_workbench 两个菜单,
--       都指向 SafetyDashboard.vue,用户感知"内容一样"。
--       现在决定:删 safety_workbench,只保留 dawa_dashboard,
--       组件内把工作台区与仪表盘区融合为单一智理安全首页。
-- 兼容: MySQL 8
-- =====================================================

DELETE FROM sys_menu WHERE code = 'safety_workbench';
