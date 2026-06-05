-- 项目部内部角色表
DROP TABLE IF EXISTS sys_org_role;
CREATE TABLE sys_org_role (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    org_id bigint NOT NULL COMMENT '项目部ID',
    role_name varchar(100) NOT NULL COMMENT '角色名称',
    role_code varchar(50) DEFAULT NULL COMMENT '角色编码',
    role_type varchar(50) DEFAULT NULL COMMENT '角色类型：pm=项目经理，safety_director=安全总监，tech_director=技术负责人等',
    sort int DEFAULT 100 COMMENT '排序',
    status int DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目部内部角色表';
