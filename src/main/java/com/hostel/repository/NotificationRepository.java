package com.hostel.repository;

import com.hostel.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Integer userId);
    long countByUserIdAndReadFalse(Integer userId);
}
