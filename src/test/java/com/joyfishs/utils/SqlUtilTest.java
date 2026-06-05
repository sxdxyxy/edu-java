package com.joyfishs.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlUtil 单元测试
 */
@DisplayName("SqlUtil 工具类测试")
class SqlUtilTest {

    @Test
    @DisplayName("escapeLikeSpecialChars - 转义百分号")
    void escapeLikeSpecialChars_shouldEscapePercentSign() {
        String result = SqlUtil.escapeLikeSpecialChars("100%");
        assertEquals("100\\%", result);
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 转义下划线")
    void escapeLikeSpecialChars_shouldEscapeUnderscore() {
        String result = SqlUtil.escapeLikeSpecialChars("test_user");
        assertEquals("test\\_user", result);
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 转义反斜杠")
    void escapeLikeSpecialChars_shouldEscapeBackslash() {
        String result = SqlUtil.escapeLikeSpecialChars("path\\file");
        assertEquals("path\\\\file", result);
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 处理空字符串")
    void escapeLikeSpecialChars_shouldHandleEmptyString() {
        assertEquals("", SqlUtil.escapeLikeSpecialChars(""));
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 处理null")
    void escapeLikeSpecialChars_shouldHandleNull() {
        assertNull(SqlUtil.escapeLikeSpecialChars(null));
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 正常字符串不变")
    void escapeLikeSpecialChars_shouldNotModifyNormalString() {
        assertEquals("normal text", SqlUtil.escapeLikeSpecialChars("normal text"));
    }

    @Test
    @DisplayName("escapeLikeSpecialChars - 转义混合特殊字符")
    void escapeLikeSpecialChars_shouldEscapeMixedSpecialChars() {
        String result = SqlUtil.escapeLikeSpecialChars("100%_test\\");
        assertEquals("100\\%\\_test\\\\", result);
    }

    @Test
    @DisplayName("escapeOrderBySql - 验证正常输入")
    void escapeOrderBySql_shouldAcceptValidInput() {
        assertEquals("name", SqlUtil.escapeOrderBySql("name"));
        assertEquals("name, age", SqlUtil.escapeOrderBySql("name, age"));
    }

    @Test
    @DisplayName("isValidOrderBySql - 验证SQL注入风险字符")
    void isValidOrderBySql_shouldRejectSqlInjection() {
        assertFalse(SqlUtil.isValidOrderBySql("name; DROP TABLE users"));
        assertFalse(SqlUtil.isValidOrderBySql("name OR 1=1"));
    }
}
