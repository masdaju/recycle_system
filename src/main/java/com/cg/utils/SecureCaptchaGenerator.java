package com.cg.utils;

import java.security.SecureRandom;

public class SecureCaptchaGenerator {
    private static final String CHAR_POOL = "0123456789";
    private static final String CHAR_POOL2 = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private static final int CAPTCHA_LENGTH = 6; // 验证码长度
    private static final SecureRandom random = new SecureRandom();

    public static String generateSecureCaptcha() {
        StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            captcha.append(CHAR_POOL.charAt(index));
        }
        return captcha.toString();
    }
    public static String generateScancode() {
        StringBuilder captcha = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHAR_POOL2.length());
            captcha.append(CHAR_POOL2.charAt(index));
        }
        return captcha.toString();
    }
    public static String generateLoginCode() {
        StringBuilder captcha = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(CHAR_POOL2.length());
            captcha.append(CHAR_POOL2.charAt(index));
        }
        return captcha.toString();
    }
}