package com.hostel.repository;

import com.hostel.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findByActiveTrueAndExpiryDateGreaterThanEqualOrderByCreatedDateDesc(LocalDate date);
    List<Notice> findAllByOrderByCreatedDateDesc();
}
