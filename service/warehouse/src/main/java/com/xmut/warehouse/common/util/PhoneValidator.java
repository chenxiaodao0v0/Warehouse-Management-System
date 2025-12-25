package com.xmut.warehouse.common.util;

import java.util.regex.Pattern;

public class PhoneValidator {
    
    // 中国手机号码正则表达式
    // 支持的号段：13x, 14x, 15x, 16x, 17x, 18x, 19x
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    
    /**
     * 验证手机号码格式是否合法
     * @param phone 待验证的手机号码
     * @return 如果是合法的手机号格式返回true，否则返回false
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // 去除可能的空格
        phone = phone.trim();
        
        // 验证长度和格式
        return PHONE_PATTERN.matcher(phone).matches();
    }
}