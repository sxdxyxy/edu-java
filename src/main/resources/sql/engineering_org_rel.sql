-- 工程项目与单位关联表
DROP TABLE IF EXISTS xm_engineering_org_rel;
CREATE TABLE xm_engineering_org_rel (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    engineering_id bigint NOT NULL COMMENT '工程项目ID',
    org_id bigint NOT NULL COMMENT '单位ID',
    role_type varchar(50) DEFAULT NULL COMMENT '角色类型：owner=建设单位，design=设计单位，construct=施工单位，supervisor=监理单位',
    create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_eng_org (engineering_id, org_id),
    KEY idx_engineering_id (engineering_id),
    KEY idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程项目与单位关联表';

-- 初始化示例数据：插入已有的工程项目和单位关联
-- 先插入测试数据（假设 sys_org 表中已有组织机构数据）
-- INSERT INTO xm_engineering_org_rel (engineering_id, org_id, role_type)
-- SELECT e.id, o.id, 'construct'
-- FROM xm_engineering_project e, sys_org o
-- WHERE e.is_delete = 0 AND o.is_delete = 0 AND o.org_type = 1
-- LIMIT 10;
