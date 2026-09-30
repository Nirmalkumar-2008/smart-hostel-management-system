package com.hostel.repository;

import com.hostel.model.Room;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByBlock(String block);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomType(RoomType roomType);

    @Query("SELECT r FROM Room r WHERE r.currentOccupancy < r.capacity")
    List<Room> findAvailableRooms();

    @Query("SELECT DISTINCT r.block FROM Room r WHERE r.block IS NOT NULL AND TRIM(r.block) <> '' ORDER BY r.block ASC")
    List<String> findDistinctBlocks();

    long countByStatus(RoomStatus status);
}
