-- 再培训管理
INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete)
VALUES ('再培训管理', 'safety_retraining', 60, 6, 1, '/safety/retraining', 'safety/RetrainingTrack', 'safety:retraining:list', 'book', 1, NOW(), 0);

-- 违章类型配置
INSERT INTO sys_menu (name, code, int_code, pid, type, router, component, permission, icon, visible, create_time, is_delete)
VALUES ('违章类型配置', 'safety_violation_type', 70, 6, 1, '/safety/violation-type', 'safety/ViolationTypeConfig', 'safety:violationtype:list', 'tool', 1, NOW(), 0);
