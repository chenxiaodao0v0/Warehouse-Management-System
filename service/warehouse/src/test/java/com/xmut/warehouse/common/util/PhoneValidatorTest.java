package com.xmut.warehouse.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhoneValidatorTest {

    @Test
    public void testValidPhoneNumbers() {
        // 测试有效的手机号码
        assertTrue(PhoneValidator.isValidPhone("13812345678"));
        assertTrue(PhoneValidator.isValidPhone("15912345678"));
        assertTrue(PhoneValidator.isValidPhone("18812345678"));
        assertTrue(PhoneValidator.isValidPhone("17712345678"));
        assertTrue(PhoneValidator.isValidPhone("19912345678"));
        assertTrue(PhoneValidator.isValidPhone("16612345678"));
        assertTrue(PhoneValidator.isValidPhone("14612345678"));
    }

    @Test
    public void testInvalidPhoneNumbers() {
        // 测试无效的手机号码
        assertFalse(PhoneValidator.isValidPhone(null));
        assertFalse(PhoneValidator.isValidPhone(""));
        assertFalse(PhoneValidator.isValidPhone(" "));
        assertFalse(PhoneValidator.isValidPhone("12345678901"));  // 不符合号段规则
        assertFalse(PhoneValidator.isValidPhone("12812345678"));  // 不符合号段规则
        assertFalse(PhoneValidator.isValidPhone("1381234567"));   // 长度不够
        assertFalse(PhoneValidator.isValidPhone("138123456789")); // 长度超出
        assertFalse(PhoneValidator.isValidPhone("1381234567a"));  // 包含非数字字符
        assertFalse(PhoneValidator.isValidPhone("1381234567@"));  // 包含特殊字符
    }
}