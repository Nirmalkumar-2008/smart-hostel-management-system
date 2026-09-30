package com.hostel.repository;

import com.hostel.model.Fee;
import com.hostel.model.enums.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {
    List<Fee> findByStudentId(Integer studentId);
    List<Fee> findByStatus(FeeStatus status);
    Optional<Fee> findByFeeCode(String feeCode);
    List<Fee> findAllByOrderByDueDateAsc();
}
