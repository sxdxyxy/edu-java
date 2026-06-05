package com.joyfishs.utils;

import com.joyfishs.utils.exception.BaseException;

/** sql操作工具类 **/
public class SqlUtil {
    /** 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序） **/
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 检查字符，防止注入绕过
     *
     * @param value
     * @return
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
            throw new BaseException("参数不符合规范，不能进行查询");

        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     *
     * @param value
     * @return
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * 转义 LIKE 查询的特殊字符 (% 和 _)
     * 安全修复: 防止 LIKE 模糊查询时被 % 和 _ 作为通配符处理
     *
     * @param value 原始搜索值
     * @return 转义后的搜索值,可安全用于 LIKE 查询
     */
    public static String escapeLikeSpecialChars(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        // 转义顺序很重要: 先转义 \, 再转义 %, 最后转义 _
        String escaped = value.replace("\\", "\\\\");
        escaped = escaped.replace("%", "\\%");
        escaped = escaped.replace("_", "\\_");
        return escaped;
    }
}
