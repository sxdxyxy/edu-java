package com.joyfishs.system.config.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Md5PasswordEncoder implements PasswordEncoder {

    private static final ThreadLocal<String> saltHolder = new ThreadLocal<>();

    public void setSalt(String salt) {
        saltHolder.set(salt);
    }

    public void clearSalt() {
        saltHolder.remove();
    }

    private static final Md5PasswordEncoder instance = new Md5PasswordEncoder();

    private Md5PasswordEncoder() {
    }

    public static Md5PasswordEncoder getInstance() {
        return instance;
    }

    // 用户密码加盐
    private String mergePasswordAndSalt(CharSequence password) {
        if (password == null) password = "";
        String salt = saltHolder.get();

        if (salt == null || "".equals(salt)) {
            return password.toString();
        } else {
            return password + salt;
        }
    }

    /**
     * MD5加密
     */
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            String passwordAndSalt = mergePasswordAndSalt(rawPassword);
            return SecureUtil.md5(passwordAndSalt);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 密码比较
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;
        return encodedPassword.equalsIgnoreCase(encode(rawPassword));
    }
}
