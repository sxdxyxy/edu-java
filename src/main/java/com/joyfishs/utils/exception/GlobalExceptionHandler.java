package com.joyfishs.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

/** 全局异常处理器 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public AjaxResult<?> baseException(BaseException e)
    {
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult<?> businessException(CustomException e) {
        log.error("业务异常:", e);
        if (null == e.getCode()) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult<?> handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(HttpStatus.NOT_FOUND.value(), "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult<?> handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public AjaxResult<?> handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public AjaxResult<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult<?> handleException(Exception e) {
        log.error("系统发生未知异常:", e);
        return AjaxResult.error("系统繁忙，请稍后再试");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult<?> validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }
}
