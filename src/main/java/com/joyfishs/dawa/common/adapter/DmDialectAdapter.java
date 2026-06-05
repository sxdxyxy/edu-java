package com.joyfishs.dawa.common.adapter;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 达梦数据库 DM8 SQL方言适配器
 * 处理MySQL语法与DM8语法的差异
 *
 * 主要适配内容：
 * 1. LIMIT offset, count -> FETCH FIRST
 * 2. CONCAT -> ||
 * 3. DATE_FORMAT -> TO_CHAR
 * 4. IFNULL -> NVL
 * 5. GROUP_CONCAT -> WM_CONCAT
 *
 * @author native-adaptation
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DmDialectAdapter implements Interceptor {

    private static final Pattern IFNULL_PATTERN = Pattern.compile(
        "IFNULL\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIMIT_PATTERN = Pattern.compile(
        "LIMIT\\s+(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile(
        "DATE_FORMAT\\s*\\(\\s*([^,]+)\\s*,\\s*'([^']+)'\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern GROUP_CONCAT_PATTERN = Pattern.compile(
        "GROUP_CONCAT\\s*\\(\\s*([^)]+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SUBSTRING_PATTERN = Pattern.compile(
        "SUBSTRING\\s*\\(\\s*([^,]+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONCAT_PATTERN = Pattern.compile(
        "CONCAT\\s*\\(\\s*([^,]+)\\s*,\\s*([^)]+)\\)", Pattern.CASE_INSENSITIVE);

    /**
     * 数据库类型标识
     */
    private String databaseType = "mysql";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 检查是否启用国产化模式
        if (!"dm".equalsIgnoreCase(databaseType)) {
            String nativeEnabled = System.getenv("NATIVE_DATABASE_ENABLED");
            if (!"true".equalsIgnoreCase(nativeEnabled)) {
                return invocation.proceed();
            }
        }

        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) target;
            BoundSql boundSql = statementHandler.getBoundSql();
            String originalSql = boundSql.getSql();

            // 转换SQL
            String convertedSql = convertSql(originalSql);

            // 修改BoundSql中的SQL
            try {
                Field sqlField = BoundSql.class.getDeclaredField("sql");
                sqlField.setAccessible(true);
                sqlField.set(boundSql, convertedSql);
            } catch (Exception e) {
                // 忽略反射错误
            }
        }

        return invocation.proceed();
    }

    /**
     * 转换SQL语句，适配达梦数据库
     */
    private String convertSql(String originalSql) {
        if (originalSql == null || originalSql.trim().isEmpty()) {
            return originalSql;
        }

        String convertedSql = originalSql;

        // 1. 转换 IFNULL -> NVL
        convertedSql = convertIfNull(convertedSql);

        // 2. 转换 CONCAT 函数
        convertedSql = convertConcat(convertedSql);

        // 3. 转换日期函数
        convertedSql = convertDateFormat(convertedSql);

        // 4. 转换 LIMIT 分页
        convertedSql = convertLimit(convertedSql);

        // 5. 转换 GROUP_CONCAT
        convertedSql = convertGroupConcat(convertedSql);

        // 6. 转换字符串函数
        convertedSql = convertStringFunctions(convertedSql);

        return convertedSql;
    }

    /**
     * 转换 IFNULL -> NVL
     */
    private String convertIfNull(String sql) {
        return IFNULL_PATTERN.matcher(sql).replaceAll("NVL(");
    }

    /**
     * 转换 CONCAT 函数 -> ||
     */
    private String convertConcat(String sql) {
        Matcher matcher = CONCAT_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "(" + matcher.group(1) + " || " + matcher.group(2) + ")");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转换 DATE_FORMAT 函数 -> TO_CHAR
     */
    private String convertDateFormat(String sql) {
        Matcher matcher = DATE_FORMAT_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String dateExpr = matcher.group(1);
            String format = convertDateFormatToDM(matcher.group(2));
            matcher.appendReplacement(sb, "TO_CHAR(" + dateExpr + ", '" + format + "')");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将MySQL日期格式转换为DM格式
     */
    private String convertDateFormatToDM(String mysqlFormat) {
        String result = mysqlFormat;
        result = result.replace("%Y", "YYYY");
        result = result.replace("%m", "MM");
        result = result.replace("%d", "DD");
        result = result.replace("%H", "HH24");
        result = result.replace("%i", "MI");
        result = result.replace("%s", "SS");
        result = result.replace("%Y-%m-%d", "YYYY-MM-DD");
        result = result.replace("%H:%i:%s", "HH24:MI:SS");
        result = result.replace("%Y-%m-%d %H:%i:%s", "YYYY-MM-DD HH24:MI:SS");
        return result;
    }

    /**
     * 转换 LIMIT 分页语法
     */
    private String convertLimit(String sql) {
        Matcher matcher = LIMIT_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int offset = Integer.parseInt(matcher.group(1));
            int count = Integer.parseInt(matcher.group(2));
            // 转换为 FETCH FIRST ... OFFSET ... 语法
            matcher.appendReplacement(sb, "FETCH FIRST " + count + " ROWS ONLY OFFSET " + offset + " ROWS");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转换 GROUP_CONCAT -> WM_CONCAT
     */
    private String convertGroupConcat(String sql) {
        return GROUP_CONCAT_PATTERN.matcher(sql).replaceAll("WM_CONCAT(',', $1)");
    }

    /**
     * 转换字符串函数
     */
    private String convertStringFunctions(String sql) {
        // SUBSTRING -> SUBSTR
        Matcher matcher = SUBSTRING_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1);
            int start = Integer.parseInt(matcher.group(2));
            int length = Integer.parseInt(matcher.group(3));
            // 达梦的SUBSTR从1开始计数
            matcher.appendReplacement(sb, "SUBSTR(" + expr + ", " + start + ", " + length + ")");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dbType = properties.getProperty("databaseType", "mysql");
        this.databaseType = dbType;
    }
}
