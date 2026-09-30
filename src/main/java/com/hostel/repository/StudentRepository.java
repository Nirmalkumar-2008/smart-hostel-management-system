package com.hostel.repository;

import com.hostel.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUserId(Integer userId);
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByEmail(String email);
    List<Student> findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(String name, String code);
}
