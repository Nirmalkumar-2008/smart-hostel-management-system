package com.hostel.service;

import com.hostel.exception.StudentNotFoundException;
import com.hostel.model.RoomAllocation;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.model.enums.RoomStatus;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository, RoomAllocationRepository roomAllocationRepository, RoomRepository roomRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.roomAllocationRepository = roomAllocationRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Student getStudentById(int id) throws StudentNotFoundException {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentByUserId(int userId) {
        return studentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        String q = query.trim();
        return studentRepository.findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(q, q);
    }

    @Transactional(readOnly = true)
    public List<Student> searchAndFilterStudents(String query, String course, Integer year, String status) {
        List<Student> students = searchStudents(query);
        return students.stream()
                .filter(s -> (course == null || course.trim().isEmpty() || s.getCourse().equalsIgnoreCase(course.trim())))
                .filter(s -> (year == null || year <= 0 || s.getYearOfStudy().equals(year)))
                .filter(s -> (status == null || status.trim().isEmpty() || s.getStatus().equalsIgnoreCase(status.trim())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Student createStudent(String username, String password, String studentCode, String fullName, String email, String phone, String gender, String course, int yearOfStudy, String emergencyContact) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (userRepository.existsByUsername(username.trim())) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        if (studentCode == null || studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Code is required.");
        }
        if (studentRepository.findByStudentCode(studentCode.trim()).isPresent()) {
            throw new IllegalArgumentException("Student Code '" + studentCode + "' is already registered.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (studentRepository.findByEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Email address '" + email + "' is already registered to another student.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(null, username.trim(), encodedPassword, "BCRYPT", Role.STUDENT, "ACTIVE");
        user = userRepository.save(user);

        Student student = new Student(null, user.getId(), studentCode.trim(), fullName.trim(), email.trim(), phone.trim(), gender.trim(), course.trim(), yearOfStudy, emergencyContact.trim(), "ACTIVE");
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudentDetails(int studentId, String studentCode, String fullName, String email, String phone, String gender, String course, int yearOfStudy, String emergencyContact, String newPassword, String confirmPassword) throws StudentNotFoundException {
        Student student = getStudentById(studentId);

        // 1. Student ID / Registration Number (studentCode) validation & update
        if (studentCode != null) {
            String trimmedCode = studentCode.trim();
            if (trimmedCode.isEmpty()) {
                throw new IllegalArgumentException("Student ID / Registration Number cannot be empty.");
            }
            if (!trimmedCode.equalsIgnoreCase(student.getStudentCode())) {
                Optional<Student> existing = studentRepository.findByStudentCode(trimmedCode);
                if (existing.isPresent() && !existing.get().getId().equals(studentId)) {
                    throw new IllegalArgumentException("Student ID already exists.");
                }
                student.setStudentCode(trimmedCode);
            }
        }

        // 2. Email uniqueness validation & update
        if (email != null && !email.trim().isEmpty()) {
            String trimmedEmail = email.trim();
            Optional<Student> existing = studentRepository.findByEmail(trimmedEmail);
            if (existing.isPresent() && !existing.get().getId().equals(studentId)) {
                throw new IllegalArgumentException("Email address '" + trimmedEmail + "' is already registered to another student.");
            }
            student.setEmail(trimmedEmail);
        }

        // 3. Update remaining student profile attributes
        if (fullName != null && !fullName.trim().isEmpty()) student.setFullName(fullName.trim());
        if (phone != null && !phone.trim().isEmpty()) student.setPhone(phone.trim());
        if (gender != null && !gender.trim().isEmpty()) student.setGender(gender.trim());
        if (course != null && !course.trim().isEmpty()) student.setCourse(course.trim());
        if (yearOfStudy > 0) student.setYearOfStudy(yearOfStudy);
        if (emergencyContact != null && !emergencyContact.trim().isEmpty()) student.setEmergencyContact(emergencyContact.trim());

        // 4. Password Change handling (optional)
        boolean hasNewPass = newPassword != null && !newPassword.trim().isEmpty();
        boolean hasConfirmPass = confirmPassword != null && !confirmPassword.trim().isEmpty();

        if (hasNewPass || hasConfirmPass) {
            if (!hasNewPass || !hasConfirmPass) {
                throw new IllegalArgumentException("Both New Password and Confirm Password are required when changing password.");
            }
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            if (newPassword.trim().length() < 4) {
                throw new IllegalArgumentException("Password does not meet the required format.");
            }

            if (student.getUserId() != null) {
                User user = userRepository.findById(student.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Associated user account not found for student."));
                String encodedPassword = passwordEncoder.encode(newPassword.trim());
                user.setPasswordSalt("BCRYPT");
                user.setPasswordHash(encodedPassword);
                userRepository.save(user);
            }
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudentDetails(int studentId, String fullName, String email, String phone, String gender, String course, int yearOfStudy, String emergencyContact) throws StudentNotFoundException {
        return updateStudentDetails(studentId, null, fullName, email, phone, gender, course, yearOfStudy, emergencyContact, null, null);
    }

    @Transactional
    public Student updateStudentProfile(int studentId, String phone, String email, String emergencyContact) throws StudentNotFoundException {
        return updateStudentDetails(studentId, null, null, email, phone, null, null, 0, emergencyContact, null, null);
    }

    @Transactional
    public Student updateStudentStatus(int studentId, String newStatus) throws StudentNotFoundException {
        Student student = getStudentById(studentId);
        student.setStatus(newStatus);
        
        // Also update associated user status
        if (student.getUserId() != null) {
            userRepository.findById(student.getUserId()).ifPresent(user -> {
                user.setStatus(newStatus);
                userRepository.save(user);
            });
        }
        
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(int studentId) throws StudentNotFoundException {
        Student student = getStudentById(studentId);

        // Deallocate active room if any
        try {
            Optional<RoomAllocation> allocOpt = roomAllocationRepository.findByStudentIdAndStatus(studentId, "ACTIVE");
            if (allocOpt.isPresent()) {
                RoomAllocation alloc = allocOpt.get();
                alloc.setStatus("DEALLOCATED");
                alloc.setDeallocatedDate(LocalDate.now());
                roomAllocationRepository.save(alloc);

                roomRepository.findById(alloc.getRoomId()).ifPresent(room -> {
                    room.setCurrentOccupancy(Math.max(0, room.getCurrentOccupancy() - 1));
                    if (room.getStatus() == RoomStatus.FULL) {
                        room.setStatus(RoomStatus.AVAILABLE);
                    }
                    roomRepository.save(room);
                });
            }
        } catch (Exception ignored) {}

        // Try hard delete; if foreign key records exist (e.g. leaves, complaints, fees), fall back to soft delete
        try {
            Integer userId = student.getUserId();
            studentRepository.delete(student);
            if (userId != null) {
                userRepository.deleteById(userId);
            }
        } catch (Exception e) {
            student.setStatus("DELETED");
            studentRepository.save(student);
            if (student.getUserId() != null) {
                userRepository.findById(student.getUserId()).ifPresent(user -> {
                    user.setStatus("INACTIVE");
                    userRepository.save(user);
                });
            }
        }
    }
}
