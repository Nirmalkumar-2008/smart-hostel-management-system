package com.hostel.service;

import com.hostel.exception.StudentNotFoundException;
import com.hostel.model.Student;
import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomAllocationRepository roomAllocationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1, "john_doe", "original_hash", "original_salt", Role.STUDENT, "ACTIVE");
        sampleStudent = new Student(1, 1, "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", "ACTIVE");
    }

    @Test
    @DisplayName("1. Add student successfully")
    void testCreateStudentSuccess() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(studentRepository.findByStudentCode("STU1001")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        Student created = studentService.createStudent("john_doe", "password123", "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789");

        assertNotNull(created);
        assertEquals("STU1001", created.getStudentCode());
        assertEquals("John Doe", created.getFullName());
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("2. Duplicate username rejected")
    void testCreateStudentDuplicateUsername() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            studentService.createStudent("john_doe", "password123", "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789")
        );

        assertTrue(ex.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("3. Duplicate student code rejected during creation")
    void testCreateStudentDuplicateCode() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(studentRepository.findByStudentCode("STU1001")).thenReturn(Optional.of(sampleStudent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            studentService.createStudent("john_doe", "password123", "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789")
        );

        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("4. Edit Student ID successfully")
    void testUpdateStudentIDSuccess() throws StudentNotFoundException {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.findByStudentCode("STU9999")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        Student updated = studentService.updateStudentDetails(1, "STU9999", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", null, null);

        assertNotNull(updated);
        assertEquals("STU9999", sampleStudent.getStudentCode());
        verify(studentRepository).save(sampleStudent);
    }

    @Test
    @DisplayName("5. Edit Student ID duplicate conflict rejected")
    void testUpdateStudentIDDuplicateRejected() {
        Student existingOtherStudent = new Student(2, 2, "STU2000", "Other Student", "other@example.com", "1234567890", "FEMALE", "EE", 2, "0000000000", "ACTIVE");
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.findByStudentCode("STU2000")).thenReturn(Optional.of(existingOtherStudent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            studentService.updateStudentDetails(1, "STU2000", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", null, null)
        );

        assertEquals("Student ID already exists.", ex.getMessage());
    }

    @Test
    @DisplayName("6. Change student password successfully")
    void testUpdatePasswordSuccess() throws StudentNotFoundException {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newEncodedHash");
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        studentService.updateStudentDetails(1, "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", "newPassword123", "newPassword123");

        verify(userRepository).save(sampleUser);
        assertEquals("$2a$10$newEncodedHash", sampleUser.getPasswordHash());
        assertEquals("BCRYPT", sampleUser.getPasswordSalt());
    }

    @Test
    @DisplayName("7. Blank password preserves existing password")
    void testUpdatePasswordBlankPreservesExisting() throws StudentNotFoundException {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        studentService.updateStudentDetails(1, "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", "", "");

        verify(userRepository, never()).save(any(User.class));
        assertEquals("original_hash", sampleUser.getPasswordHash());
        assertEquals("original_salt", sampleUser.getPasswordSalt());
    }

    @Test
    @DisplayName("8. Mismatching passwords rejected")
    void testUpdatePasswordMismatchRejected() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            studentService.updateStudentDetails(1, "STU1001", "John Doe", "john@example.com", "9876543210", "MALE", "CS", 1, "9123456789", "pass123", "pass456")
        );

        assertEquals("Passwords do not match.", ex.getMessage());
    }

    @Test
    @DisplayName("9. Delete student successfully")
    void testDeleteStudentSuccess() throws StudentNotFoundException {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleStudent));
        when(roomAllocationRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());

        studentService.deleteStudent(1);

        verify(studentRepository).delete(sampleStudent);
        verify(userRepository).deleteById(1);
    }

    @Test
    @DisplayName("10. Student not found exception")
    void testStudentNotFound() {
        when(studentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(99));
    }
}
