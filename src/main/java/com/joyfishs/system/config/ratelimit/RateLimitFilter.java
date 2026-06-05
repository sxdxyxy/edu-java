package com.joyfishs.system.config.ratelimit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 简单的基于IP的速率限制过滤器
 * 安全修复: 防止暴力破解认证端点
 *
 * 配置说明:
 * - 使用内存存储(单实例有效),生产环境建议使用Redis
 * - 默认限制: 60秒内最多30次请求
 * - 适用于登录、短信验证码等敏感端点
 */
@Slf4j
@Component
public class RateLimitFilter implements Filter {

    /**
     * IP -> (时间窗口 -> 请求计数)
     */
    private final Map<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    /**
     * 时间窗口大小(秒)
     */
    private static final long WINDOW_SIZE_SECONDS = 60;

    /**
     * 窗口内最大请求次数
     */
    private static final int MAX_REQUESTS_PER_WINDOW = 30;

    /**
     * 被限制的IP缓存时间(秒)
     */
    private static final long BLOCK_DURATION_SECONDS = 300;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RateLimitFilter initialized: window={}s, max={} requests",
                WINDOW_SIZE_SECONDS, MAX_REQUESTS_PER_WINDOW);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);
        String requestUri = httpRequest.getRequestURI();

        // 只对认证相关端点进行限流
        if (shouldRateLimit(requestUri)) {
            if (!checkRateLimit(clientIp)) {
                log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, requestUri);
                sendRateLimitResponse(httpResponse);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * 判断请求是否需要限流
     */
    private boolean shouldRateLimit(String uri) {
        return uri.contains("/login")
            || uri.contains("/studentLogin")
            || uri.contains("/captchaLogin")
            || uri.contains("/WeiXin")
            || uri.contains("/faceLogin")
            || uri.contains("/sms/getCode")
            || uri.contains("/register");
    }

    /**
     * 检查速率限制
     */
    private boolean checkRateLimit(String clientIp) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - TimeUnit.SECONDS.toMillis(WINDOW_SIZE_SECONDS);

        RateLimitEntry entry = rateLimitMap.compute(clientIp, (key, existing) -> {
            if (existing == null) {
                return new RateLimitEntry(currentTime);
            }

            // 如果窗口已过期,重置
            if (existing.windowStart < windowStart) {
                return new RateLimitEntry(currentTime);
            }

            // 增加计数
            existing.count.incrementAndGet();
            return existing;
        });

        return entry.count.get() <= MAX_REQUESTS_PER_WINDOW;
    }

    /**
     * 发送限流响应
     */
    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Retry-After", String.valueOf(WINDOW_SIZE_SECONDS));

        AjaxResult<?> result = AjaxResult.error("请求过于频繁,请稍后再试");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public void destroy() {
        rateLimitMap.clear();
        log.info("RateLimitFilter destroyed");
    }

    /**
     * 速率限制条目
     */
    private static class RateLimitEntry {
        final long windowStart;
        final AtomicInteger count = new AtomicInteger(1);

        RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
