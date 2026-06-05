package com.joyfishs.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.convert.Convert;

public class HttpServletUtil {

    public static ServletRequestAttributes getServletRequestAttributes(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if(requestAttributes == null) return null;

        return (ServletRequestAttributes) requestAttributes;
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return requestAttributes.getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return
     */
    public static HttpServletResponse getResponse(){
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return requestAttributes.getResponse();
    }

    /**
     * 获取String参数
     *
     * @param name
     * @return
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     *
     * @param name
     * @return
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

}
