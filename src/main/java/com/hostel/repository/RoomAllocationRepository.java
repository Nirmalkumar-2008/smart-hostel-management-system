package com.hostel.repository;

import com.hostel.model.RoomAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Integer> {
    Optional<RoomAllocation> findByStudentIdAndStatus(Integer studentId, String status);
    List<RoomAllocation> findByRoomIdAndStatus(Integer roomId, String status);
    List<RoomAllocation> findByStudentId(Integer studentId);
    List<RoomAllocation> findByStatus(String status);
}
