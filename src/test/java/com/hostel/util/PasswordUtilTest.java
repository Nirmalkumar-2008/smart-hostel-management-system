package com.hostel.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Should generate non-null 32-char hex salt")
    void testSaltGeneration() {
        String salt = PasswordUtil.generateSalt();
        assertNotNull(salt);
        assertEquals(32, salt.length());
    }

    @Test
    @DisplayName("Should generate exact hash matching seed data")
    void testExactHash() {
        String adminSalt = "salt_admin_123456";
        String adminHash = PasswordUtil.hashPassword("admin123", adminSalt);
        System.out.println("ADMIN_HASH: " + adminHash);

        String studentSalt = "salt_student_1234";
        String studentHash = PasswordUtil.hashPassword("student123", studentSalt);
        System.out.println("STUDENT_HASH: " + studentHash);
    }
}
