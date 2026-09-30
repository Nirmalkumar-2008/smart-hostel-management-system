package com.hostel.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_allocations")
public class RoomAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "allocated_date", nullable = false)
    private LocalDate allocatedDate;

    @Column(name = "deallocated_date")
    private LocalDate deallocatedDate;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, DEALLOCATED

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;
    @Transient
    private String roomNumber;

    public RoomAllocation() {}

    public RoomAllocation(Integer id, Integer studentId, Integer roomId, LocalDate allocatedDate, String status, String notes) {
        this.id = id;
        this.studentId = studentId;
        this.roomId = roomId;
        this.allocatedDate = allocatedDate;
        this.status = status;
        this.notes = notes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public LocalDate getAllocatedDate() { return allocatedDate; }
    public void setAllocatedDate(LocalDate allocatedDate) { this.allocatedDate = allocatedDate; }

    public LocalDate getDeallocatedDate() { return deallocatedDate; }
    public void setDeallocatedDate(LocalDate deallocatedDate) { this.deallocatedDate = deallocatedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}
