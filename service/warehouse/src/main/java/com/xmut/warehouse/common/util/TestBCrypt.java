package com.xmut.warehouse.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 临时生成合法的BCrypt加密串
 */
public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456"; // 明文密码
        String encodedPassword = encoder.encode(rawPassword); // 生成合法加密串
        System.out.println("123456对应的BCrypt加密串：" + encodedPassword);
    }
}
