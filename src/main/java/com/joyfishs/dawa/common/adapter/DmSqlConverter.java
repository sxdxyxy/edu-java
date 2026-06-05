package com.joyfishs.dawa.common.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL方言自动转换工具
 * 将MySQL语法转换为达梦DM8语法
 *
 * @author native-adaptation
 */
public class DmSqlConverter {

    // 编译常用的正则表达式
    private static final Pattern IFNULL_PATTERN = Pattern.compile(
        "IFNULL\\s*\\(", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern LIMIT_OFFSET_PATTERN = Pattern.compile(
        "LIMIT\\s+(\\d+)\\s*,\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern LIMIT_ONLY_PATTERN = Pattern.compile(
        "LIMIT\\s+(\\d+)(?!\\s*,)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile(
        "DATE_FORMAT\\s*\\(\\s*([^,]+)\\s*,\\s*'([^']+)'\\s*\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern GROUP_CONCAT_PATTERN = Pattern.compile(
        "GROUP_CONCAT\\s*\\(\\s*([^)]+)\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern CONCAT_PATTERN = Pattern.compile(
        "CONCAT\\s*\\(\\s*([^,]+)\\s*,\\s*([^)]+)\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern SUBSTRING_PATTERN = Pattern.compile(
        "SUBSTRING\\s*\\(\\s*([^,]+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern NOW_PATTERN = Pattern.compile(
        "NOW\\s*\\(\\s*\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern CURDATE_PATTERN = Pattern.compile(
        "CURDATE\\s*\\(\\s*\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern UNIX_TIMESTAMP_PATTERN = Pattern.compile(
        "UNIX_TIMESTAMP\\s*\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern FROM_UNIXTIME_PATTERN = Pattern.compile(
        "FROM_UNIXTIME\\s*\\(([^)]+)(?:\\s*,\\s*'([^']*)')?\\)", Pattern.CASE_INSENSITIVE);

    /**
     * 转换SQL语句
     */
    public static String convert(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }

        String result = sql;

        // 按顺序执行转换
        result = convertIfNull(result);
        result = convertConcat(result);
        result = convertDateFormat(result);
        result = convertLimit(result);
        result = convertGroupConcat(result);
        result = convertStringFunctions(result);
        result = convertDateTimeFunctions(result);
        result = convertTimestampFunctions(result);

        return result;
    }

    /**
     * 转换 IFNULL -> NVL
     */
    private static String convertIfNull(String sql) {
        return IFNULL_PATTERN.matcher(sql).replaceAll("NVL(");
    }

    /**
     * 转换 CONCAT -> ||
     */
    private static String convertConcat(String sql) {
        Matcher matcher = CONCAT_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String first = matcher.group(1).trim();
            String second = matcher.group(2).trim().replaceAll("\\)\\s*$", "");
            matcher.appendReplacement(sb, "(" + first + " || " + second + ")");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转换 DATE_FORMAT -> TO_CHAR
     */
    private static String convertDateFormat(String sql) {
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
    private static String convertDateFormatToDM(String mysqlFormat) {
        String result = mysqlFormat;
        // 年
        result = result.replace("%Y", "YYYY");
        result = result.replace("%y", "YY");
        // 月
        result = result.replace("%m", "MM");
        // 日
        result = result.replace("%d", "DD");
        result = result.replace("%e", "D");
        // 时分秒
        result = result.replace("%H", "HH24");
        result = result.replace("%h", "HH");
        result = result.replace("%I", "HH");
        result = result.replace("%i", "MI");
        result = result.replace("%s", "SS");
        result = result.replace("%r", "HH24:MI:SS");
        result = result.replace("%T", "HH24:MI:SS");
        // 周
        result = result.replace("%W", "DAY");
        result = result.replace("%a", "DY");
        result = result.replace("%w", "D");
        result = result.replace("%U", "WW");
        result = result.replace("%u", "WW");
        // 其他
        result = result.replace("%p", "AM");
        result = result.replace("%j", "DDD");
        
        return result;
    }

    /**
     * 转换 LIMIT 分页语法
     */
    private static String convertLimit(String sql) {
        // 处理 LIMIT offset, count
        Matcher matcher = LIMIT_OFFSET_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int offset = Integer.parseInt(matcher.group(1));
            int count = Integer.parseInt(matcher.group(2));
            matcher.appendReplacement(sb, "OFFSET " + offset + " ROWS FETCH NEXT " + count + " ROWS ONLY");
        }
        matcher.appendTail(sb);
        
        // 处理 LIMIT count (无offset)
        String result = sb.toString();
        matcher = LIMIT_ONLY_PATTERN.matcher(result);
        sb = new StringBuffer();
        while (matcher.find()) {
            int count = Integer.parseInt(matcher.group(1));
            matcher.appendReplacement(sb, "FETCH FIRST " + count + " ROWS ONLY");
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    /**
     * 转换 GROUP_CONCAT -> WM_CONCAT
     */
    private static String convertGroupConcat(String sql) {
        Matcher matcher = GROUP_CONCAT_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1);
            // DM8的WM_CONCAT默认使用逗号分隔，如需其他分隔符请手动处理
            if (expr.contains("ORDER BY")) {
                // DM8的WM_CONCAT不支持ORDER BY，需要特殊处理
                matcher.appendReplacement(sb, "LISTAGG(" + convertGroupConcatExpression(expr) + ", ',') WITHIN GROUP (ORDER BY " + extractOrderBy(expr) + ")");
            } else {
                matcher.appendReplacement(sb, "WM_CONCAT(',', " + expr + ")");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 处理 GROUP_CONCAT 表达式
     */
    private static String convertGroupConcatExpression(String expr) {
        // 移除 ORDER BY 部分
        int orderByIndex = expr.indexOf("ORDER BY");
        if (orderByIndex > 0) {
            return expr.substring(0, orderByIndex).trim();
        }
        // 移除 SEPARATOR 部分
        int sepIndex = expr.indexOf("SEPARATOR");
        if (sepIndex > 0) {
            return expr.substring(0, sepIndex).trim();
        }
        return expr;
    }

    /**
     * 提取 ORDER BY 子句
     */
    private static String extractOrderBy(String expr) {
        int orderByIndex = expr.indexOf("ORDER BY");
        if (orderByIndex > 0) {
            int endIndex = expr.indexOf(")", orderByIndex);
            if (endIndex > orderByIndex) {
                return expr.substring(orderByIndex + 9, endIndex);
            }
        }
        return "";
    }

    /**
     * 转换字符串函数
     */
    private static String convertStringFunctions(String sql) {
        String result = sql;
        
        // SUBSTRING -> SUBSTR
        Matcher matcher = SUBSTRING_PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1);
            int start = Integer.parseInt(matcher.group(2));
            int length = Integer.parseInt(matcher.group(3));
            // 达梦的SUBSTR从1开始
            matcher.appendReplacement(sb, "SUBSTR(" + expr + ", " + start + ", " + length + ")");
        }
        matcher.appendTail(sb);
        result = sb.toString();
        
        // 其他字符串函数的批量替换
        result = result.replaceAll("(?i)CHAR_LENGTH\\s*\\(", "LENGTH(");
        result = result.replaceAll("(?i)LOCATE\\s*\\(", "INSTR(");
        result = result.replaceAll("(?i)REPEAT\\s*\\(", "RPAD(");
        
        return result;
    }

    /**
     * 转换日期时间函数
     */
    private static String convertDateTimeFunctions(String sql) {
        String result = sql;
        
        // NOW() -> SYSDATE
        result = NOW_PATTERN.matcher(result).replaceAll("SYSDATE");
        
        // CURDATE() -> TRUNC(SYSDATE)
        result = CURDATE_PATTERN.matcher(result).replaceAll("TRUNC(SYSDATE)");
        
        // UTC_DATE() -> TRUNC(SYSDATE) - 简化的时区处理
        result = result.replaceAll("(?i)UTC_DATE\\s*\\(\\s*\\)", "TRUNC(SYSDATE)");
        
        return result;
    }

    /**
     * 转换时间戳函数
     */
    private static String convertTimestampFunctions(String sql) {
        String result = sql;
        
        // UNIX_TIMESTAMP() -> EPOCH_SECONDS
        // DM8中使用 EXTRACT(EPOCH FROM ...) 或自定义函数
        result = result.replaceAll("(?i)UNIX_TIMESTAMP\\s*\\(\\s*\\)", 
            "FLOOR(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP))");
        
        // UNIX_TIMESTAMP(date) -> EPOCH from date
        result = result.replaceAll("(?i)UNIX_TIMESTAMP\\s*\\(\\s*([^)]+)\\)", 
            "FLOOR(EXTRACT(EPOCH FROM $1))");
        
        // FROM_UNIXTIME(timestamp) -> TIMESTAMP
        result = result.replaceAll("(?i)FROM_UNIXTIME\\s*\\(([^)]+)\\)", 
            "TO_DATE('1970-01-01','YYYY-MM-DD') + ($1 / 86400) - 1/8640");
        
        return result;
    }

    /**
     * 获取类型映射表
     */
    public static Map<String, String> getTypeMappings() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("INT", "INT");
        mappings.put("BIGINT", "BIGINT");
        mappings.put("VARCHAR", "VARCHAR");
        mappings.put("TEXT", "CLOB");
        mappings.put("DATETIME", "TIMESTAMP");
        mappings.put("DECIMAL", "NUMERIC");
        mappings.put("TINYINT", "SMALLINT");
        mappings.put("FLOAT", "FLOAT");
        mappings.put("DOUBLE", "DOUBLE");
        mappings.put("BLOB", "BLOB");
        mappings.put("CHAR", "CHAR");
        mappings.put("ENUM", "VARCHAR");
        mappings.put("SET", "VARCHAR");
        return mappings;
    }

    /**
     * 获取特殊函数映射
     */
    public static Map<String, String> getFunctionMappings() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("IFNULL", "NVL");
        mappings.put("COALESCE", "NVL");
        mappings.put("DATE_FORMAT", "TO_CHAR");
        mappings.put("GROUP_CONCAT", "WM_CONCAT");
        mappings.put("CONCAT", "||");
        mappings.put("SUBSTRING", "SUBSTR");
        mappings.put("SUBSTR", "SUBSTR"); // 保持一致
        mappings.put("TRIM", "TRIM");
        mappings.put("LTRIM", "LTRIM");
        mappings.put("RTRIM", "RTRIM");
        mappings.put("LPAD", "LPAD");
        mappings.put("RPAD", "RPAD");
        mappings.put("LENGTH", "LENGTH");
        mappings.put("CHAR_LENGTH", "LENGTH");
        mappings.put("LOCATE", "INSTR");
        mappings.put("NOW", "SYSDATE");
        mappings.put("CURDATE", "TRUNC(SYSDATE)");
        mappings.put("CURTIME", "SYSTIMESTAMP");
        mappings.put("LAST_INSERT_ID", "LASTVAL"); // DM8中使用序列
        return mappings;
    }
}
