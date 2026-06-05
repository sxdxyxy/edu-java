package com.joyfishs.utils.exception;

/**
 * Sms异常类
 *
 * @author Lion Li
 */
public class SmsException extends RuntimeException {

    private static final long serialVersionUID = -54439614557944578L;

    public SmsException(String msg) {
        super(msg);
    }

}
