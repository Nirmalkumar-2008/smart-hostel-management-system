package com.hostel.repository;

import com.hostel.model.Complaint;
import com.hostel.model.enums.ComplaintPriority;
import com.hostel.model.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByCalculatedPriority(ComplaintPriority priority);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
