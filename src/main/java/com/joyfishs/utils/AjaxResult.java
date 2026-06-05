package com.joyfishs.utils;

import java.util.HashMap;

import org.springframework.http.HttpStatus;

/** 操作消息提醒 **/
public class AjaxResult<T> extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /** 初始化一个新创建的 AjaxResult<?> 对象，使其表示一个空消息 **/
    public AjaxResult() { }

    /**
     * 初始化一个新创建 of AjaxResult<?> 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg){
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建 of AjaxResult<?> 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, T data){
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) super.put(DATA_TAG, data);
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success() { return AjaxResult.success("操作成功"); }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(T data) { return AjaxResult.success("操作成功", data); }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg) { return AjaxResult.success(msg, null); }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg, T data) { return new AjaxResult<T>(HttpStatus.OK.value(), msg, data); }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static <T> AjaxResult<T> error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg) { return AjaxResult.error(msg, null); }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg, T data) { return new AjaxResult<T>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data); }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(int code, String msg) { return new AjaxResult<T>(code, msg, null); }

    @Override
    public AjaxResult<T> put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
