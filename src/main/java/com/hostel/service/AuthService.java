package com.hostel.service;

import com.hostel.exception.InvalidLoginException;
import com.hostel.model.Admin;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.repository.AdminRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.UserRepository;
import com.hostel.util.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AdminRepository adminRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static class UserSession {
        private final User user;
        private final Admin admin;
        private final Student student;

        public UserSession(User user, Admin admin, Student student) {
            this.user = user;
            this.admin = admin;
            this.student = student;
        }

        public User getUser() { return user; }
        public Admin getAdmin() { return admin; }
        public Student getStudent() { return student; }
        public boolean isAdmin() { return user != null && user.getRole() == Role.ADMIN; }
        public boolean isStudent() { return user != null && user.getRole() == Role.STUDENT; }
    }

    @Transactional(readOnly = true)
    public UserSession login(String username, String password) throws InvalidLoginException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidLoginException("Invalid username or password."));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new InvalidLoginException("Account is " + user.getStatus() + ". Please contact administrator.");
        }

        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getPasswordSalt());
        if (!valid) {
            try {
                valid = passwordEncoder.matches(password, user.getPasswordHash());
            } catch (Exception ignored) {}
        }

        if (!valid) {
            throw new InvalidLoginException("Invalid username or password.");
        }

        Admin admin = null;
        Student student = null;

        if (user.getRole() == Role.ADMIN) {
            admin = adminRepository.findByUserId(user.getId()).orElse(null);
        } else if (user.getRole() == Role.STUDENT) {
            student = studentRepository.findByUserId(user.getId()).orElse(null);
        }

        return new UserSession(user, admin, student);
    }
}
