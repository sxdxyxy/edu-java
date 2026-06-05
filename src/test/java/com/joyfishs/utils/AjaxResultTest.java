package com.joyfishs.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AjaxResultTest {

    @Test
    void success_WithNoArgs_ReturnsOk() {
        AjaxResult<?> result = AjaxResult.success();
        assertEquals(HttpStatus.OK.value(), result.get("code"));
        assertEquals("操作成功", result.get("msg"));
    }

    @Test
    void success_WithMessage_ReturnsOk() {
        AjaxResult<?> result = AjaxResult.success("自定义成功消息");
        assertEquals(HttpStatus.OK.value(), result.get("code"));
        assertEquals("自定义成功消息", result.get("msg"));
    }

    @Test
    void success_WithData_ReturnsOkWithData() {
        AjaxResult<?> result = AjaxResult.success("成功", "testData");
        assertEquals(HttpStatus.OK.value(), result.get("code"));
        assertEquals("成功", result.get("msg"));
        assertEquals("testData", result.get("data"));
    }

    @Test
    void error_WithNoArgs_ReturnsError() {
        AjaxResult<?> result = AjaxResult.error();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.get("code"));
        assertEquals("操作失败", result.get("msg"));
    }

    @Test
    void error_WithMessage_ReturnsErrorWithMessage() {
        AjaxResult<?> result = AjaxResult.error("自定义错误消息");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.get("code"));
        assertEquals("自定义错误消息", result.get("msg"));
    }

    @Test
    void error_WithCodeAndMessage_ReturnsError() {
        AjaxResult<?> result = AjaxResult.error(400, "请求错误");
        assertEquals(400, result.get("code"));
        assertEquals("请求错误", result.get("msg"));
    }

    @Test
    void put_ReturnsSameInstance() {
        AjaxResult<?> result = new AjaxResult<>();
        AjaxResult<?> returned = result.put("key", "value");
        assertSame(result, returned);
        assertEquals("value", result.get("key"));
    }

    @Test
    void success_WithNullData_ReturnsOk() {
        AjaxResult<?> result = AjaxResult.success("成功", null);
        assertEquals(HttpStatus.OK.value(), result.get("code"));
        assertEquals("成功", result.get("msg"));
        assertNull(result.get("data"));
    }

    @Test
    void error_WithCodeMessageAndData_ReturnsError() {
        AjaxResult<?> result = AjaxResult.error("错误", "errorData");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.get("code"));
        assertEquals("错误", result.get("msg"));
        assertEquals("errorData", result.get("data"));
    }
}
