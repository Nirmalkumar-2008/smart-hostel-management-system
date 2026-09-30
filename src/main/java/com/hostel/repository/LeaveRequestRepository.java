package com.hostel.repository;

import com.hostel.model.LeaveRequest;
import com.hostel.model.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
}
