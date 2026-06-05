package com.joyfishs.dawa.common.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库配置帮助类
 * 支持MySQL与DM8双环境配置切换
 *
 * @author native-adaptation
 */
@Component
public class DatabaseConfigHelper {

    private final Environment environment;

    public DatabaseConfigHelper(Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取当前数据库类型
     */
    public String getDatabaseType() {
        // 优先从环境变量获取
        String dbType = System.getenv("DATABASE_TYPE");
        if (dbType != null) {
            return dbType;
        }
        
        // 从配置文件获取
        String driverClass = environment.getProperty("spring.datasource.driver-class-name", "");
        if (driverClass.contains("dm.")) {
            return "dm";
        }
        
        return "mysql";
    }

    /**
     * 是否启用国产化模式
     */
    public boolean isNativeMode() {
        return "dm".equalsIgnoreCase(getDatabaseType()) || 
               "true".equalsIgnoreCase(System.getenv("NATIVE_DATABASE_ENABLED"));
    }

    /**
     * 获取分页方言
     */
    public String getPageHelperDialect() {
        if (isNativeMode()) {
            return "oracle";
        }
        return "mysql";
    }

    /**
     * 获取SQL函数映射
     */
    public Map<String, String> getSqlFunctionMappings() {
        Map<String, String> mappings = new HashMap<>();
        
        if (isNativeMode()) {
            // DM8 函数映射
            mappings.put("IFNULL", "NVL");
            mappings.put("DATE_FORMAT", "TO_CHAR");
            mappings.put("GROUP_CONCAT", "WM_CONCAT");
            mappings.put("CONCAT", "||");
            mappings.put("SUBSTRING", "SUBSTR");
            mappings.put("NOW", "SYSDATE");
            mappings.put("CURDATE", "TRUNC(SYSDATE)");
            mappings.put("UNIX_TIMESTAMP", " extract(epoch from ");
            mappings.put("FROM_UNIXTIME", " to_char(to_date(");
        } else {
            // MySQL 函数映射（空表示不需要转换）
            mappings.put("IFNULL", "IFNULL");
            mappings.put("DATE_FORMAT", "DATE_FORMAT");
            mappings.put("GROUP_CONCAT", "GROUP_CONCAT");
            mappings.put("CONCAT", "CONCAT");
            mappings.put("SUBSTRING", "SUBSTRING");
            mappings.put("NOW", "NOW()");
            mappings.put("CURDATE", "CURDATE()");
            mappings.put("UNIX_TIMESTAMP", "UNIX_TIMESTAMP");
            mappings.put("FROM_UNIXTIME", "FROM_UNIXTIME");
        }
        
        return mappings;
    }

    /**
     * 获取分页SQL语法
     */
    public String wrapLimitSql(String sql, int offset, int limit) {
        if (isNativeMode()) {
            // DM8 使用 FETCH FIRST ... OFFSET ...
            return sql + " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }
        // MySQL 使用 LIMIT offset, limit
        return sql + " LIMIT " + offset + ", " + limit;
    }

    /**
     * 获取自增ID语法
     */
    public String getAutoIncrementSyntax() {
        if (isNativeMode()) {
            return "GENERATED ALWAYS AS IDENTITY";
        }
        return "AUTO_INCREMENT";
    }

    /**
     * 获取序列创建语法
     */
    public String createSequenceSql(String sequenceName, long startValue) {
        if (isNativeMode()) {
            return "CREATE SEQUENCE " + sequenceName + " START WITH " + startValue + " INCREMENT BY 1";
        }
        return null; // MySQL不需要手动创建序列
    }
}
