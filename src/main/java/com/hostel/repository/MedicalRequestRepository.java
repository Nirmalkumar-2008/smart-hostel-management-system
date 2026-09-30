package com.hostel.repository;

import com.hostel.model.MedicalRequest;
import com.hostel.model.enums.MedicalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRequestRepository extends JpaRepository<MedicalRequest, Integer> {
    List<MedicalRequest> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<MedicalRequest> findByStatus(MedicalStatus status);
    List<MedicalRequest> findAllByOrderByCreatedAtDesc();
}
