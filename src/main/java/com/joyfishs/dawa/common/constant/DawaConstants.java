package com.joyfishs.dawa.common.constant;

/**
 * 系统常量配置
 * 包含国产化适配相关配置
 *
 * @author native-adaptation
 */
public class DawaConstants {

    /**
     * 数据库类型
     */
    public static final String DATABASE_TYPE_MYSQL = "mysql";
    public static final String DATABASE_TYPE_DM = "dm";

    /**
     * 环境变量 - 启用国产化数据库
     */
    public static final String ENV_NATIVE_DATABASE_ENABLED = "NATIVE_DATABASE_ENABLED";
    
    /**
     * 环境变量 - 数据库类型
     */
    public static final String ENV_DATABASE_TYPE = "DATABASE_TYPE";

    /**
     * 环境变量 - 达梦数据库配置
     */
    public static final String ENV_DM_HOST = "DM_HOST";
    public static final String ENV_DM_PORT = "DM_PORT";
    public static final String ENV_DM_DATABASE = "DM_DATABASE";
    public static final String ENV_DM_USERNAME = "DM_USERNAME";
    public static final String ENV_DM_PASSWORD = "DM_PASSWORD";

    /**
     * 获取当前数据库类型
     */
    public static String getDatabaseType() {
        String dbType = System.getenv(ENV_DATABASE_TYPE);
        return dbType != null ? dbType : DATABASE_TYPE_MYSQL;
    }

    /**
     * 是否启用国产化模式
     */
    public static boolean isNativeMode() {
        String nativeEnabled = System.getenv(ENV_NATIVE_DATABASE_ENABLED);
        return "true".equalsIgnoreCase(nativeEnabled) || "dm".equalsIgnoreCase(getDatabaseType());
    }
}
