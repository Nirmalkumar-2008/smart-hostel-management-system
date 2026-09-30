package com.hostel.repository;

import com.hostel.model.WaitingListEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingListEntry, Integer> {
    List<WaitingListEntry> findByStatusOrderByPriorityScoreDescRequestDateAsc(String status);
    List<WaitingListEntry> findByStudentId(Integer studentId);
}
