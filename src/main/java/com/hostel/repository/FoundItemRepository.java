package com.hostel.repository;

import com.hostel.model.FoundItem;
import com.hostel.model.enums.FoundItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Integer> {
    List<FoundItem> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<FoundItem> findByStatus(FoundItemStatus status);
    List<FoundItem> findAllByOrderByCreatedAtDesc();
}
