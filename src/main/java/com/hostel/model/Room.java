package com.hostel.model;

import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_number", nullable = false, unique = true, length = 50)
    private String roomNumber;

    @Column(nullable = false, length = 100)
    private String block = "A";

    @Column(nullable = false)
    private Integer floor = 1;

    @Column(nullable = false)
    private Integer capacity = 2;

    @Column(name = "current_occupancy", nullable = false)
    private Integer currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 50)
    private RoomType roomType = RoomType.DOUBLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "base_monthly_rent", nullable = false)
    private Double baseMonthlyRent = 5000.00;

    @Column(length = 255)
    private String description;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Room() {}

    public Room(Integer id, String roomNumber, String block, Integer floor, Integer capacity, Integer currentOccupancy, RoomType roomType, RoomStatus status, Double baseMonthlyRent) {
        this(id, roomNumber, block, floor, capacity, currentOccupancy, roomType, status, baseMonthlyRent, null);
    }

    public Room(Integer id, String roomNumber, String block, Integer floor, Integer capacity, Integer currentOccupancy, RoomType roomType, RoomStatus status, Double baseMonthlyRent, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.block = block;
        this.floor = floor;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.roomType = roomType;
        this.status = status;
        this.baseMonthlyRent = baseMonthlyRent;
        this.description = description;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Double getBaseMonthlyRent() { return baseMonthlyRent; }
    public void setBaseMonthlyRent(Double baseMonthlyRent) { this.baseMonthlyRent = baseMonthlyRent; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isFull() {
        return currentOccupancy != null && capacity != null && currentOccupancy >= capacity;
    }

    public int getAvailableCapacity() {
        if (capacity == null) return 0;
        int occ = (currentOccupancy == null) ? 0 : currentOccupancy;
        return Math.max(0, capacity - occ);
    }

    public int getAvailableBeds() {
        return getAvailableCapacity();
    }

    public int getOccupancyPercentage() {
        if (capacity == null || capacity <= 0) return 0;
        int occ = (currentOccupancy == null) ? 0 : currentOccupancy;
        return (int) Math.round(((double) occ / capacity) * 100);
    }

    public void updateComputedStatus() {
        if (status == RoomStatus.MAINTENANCE) {
            return; // Preserve manual maintenance status
        }
        int occ = (currentOccupancy == null) ? 0 : currentOccupancy;
        int cap = (capacity == null) ? 0 : capacity;

        if (occ >= cap && cap > 0) {
            this.status = RoomStatus.FULL;
        } else if (occ > 0 && occ < cap) {
            this.status = RoomStatus.PARTIALLY_OCCUPIED;
        } else {
            this.status = RoomStatus.AVAILABLE;
        }
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
