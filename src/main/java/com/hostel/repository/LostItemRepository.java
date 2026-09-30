package com.hostel.repository;

import com.hostel.model.LostItem;
import com.hostel.model.enums.LostItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Integer> {
    List<LostItem> findByStudentIdOrderByCreatedAtDesc(Integer studentId);
    List<LostItem> findByStatus(LostItemStatus status);
    List<LostItem> findAllByOrderByCreatedAtDesc();
}
