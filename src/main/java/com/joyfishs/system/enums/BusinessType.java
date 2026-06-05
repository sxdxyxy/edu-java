package com.joyfishs.system.enums;

/** 业务操作类型 **/
public enum BusinessType {
    /** 其它 **/
    OTHER,

    /** 新增 **/
    INSERT,

    /** 修改 **/
    UPDATE,

    /** 删除 **/
    DELETE,

    /** 查询 **/
    SELECT,

    /** 授权 **/
    GRANT,

    /** 导出 **/
    EXPORT,

    /** 导入 **/
    IMPORT,

    /** 强退 **/
    FORCE,

    /** 生成代码 **/
    GENCODE,

    /** 清空数据 **/
    CLEAN,

    /** 登陆 **/
    LOGIN,
}
