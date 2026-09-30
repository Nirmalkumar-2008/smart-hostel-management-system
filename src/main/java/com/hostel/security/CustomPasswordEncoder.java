package com.hostel.security;

import com.hostel.model.User;
import com.hostel.repository.UserRepository;
import com.hostel.util.PasswordUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public CustomPasswordEncoder(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bCrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;

        // 1. Try BCrypt match
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bCrypt.matches(rawPassword, encodedPassword);
        }

        // 2. Try legacy seed SHA-256 with fixed salts
        String adminHash = PasswordUtil.hashPassword(rawPassword.toString(), "salt_admin_123456");
        if (encodedPassword.equalsIgnoreCase(adminHash)) {
            return true;
        }

        String studentHash = PasswordUtil.hashPassword(rawPassword.toString(), "salt_student_1234");
        if (encodedPassword.equalsIgnoreCase(studentHash)) {
            return true;
        }

        // 3. Dynamic SHA-256 verification using stored salt if present in DB
        try {
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> encodedPassword.equalsIgnoreCase(u.getPasswordHash()))
                    .findFirst();
            if (userOpt.isPresent() && userOpt.get().getPasswordSalt() != null) {
                String salt = userOpt.get().getPasswordSalt();
                if (!"BCRYPT".equalsIgnoreCase(salt)) {
                    String computedHash = PasswordUtil.hashPassword(rawPassword.toString(), salt);
                    if (encodedPassword.equalsIgnoreCase(computedHash)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}

        return false;
    }
}
