package com.hostel.model;

import com.hostel.model.enums.RoomType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list")
public class WaitingListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "preferred_block", length = 100)
    private String preferredBlock;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_room_type")
    private RoomType preferredRoomType;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore = 50;

    @Column(nullable = false, length = 20)
    private String status = "WAITING"; // WAITING, ALLOCATED, CANCELLED

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;
    @Transient
    private String studentCode;

    public WaitingListEntry() {}

    public WaitingListEntry(Integer id, Integer studentId, String preferredBlock, RoomType preferredRoomType, LocalDate requestDate, Integer priorityScore, String status) {
        this.id = id;
        this.studentId = studentId;
        this.preferredBlock = preferredBlock;
        this.preferredRoomType = preferredRoomType;
        this.requestDate = requestDate;
        this.priorityScore = priorityScore;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getPreferredBlock() { return preferredBlock; }
    public void setPreferredBlock(String preferredBlock) { this.preferredBlock = preferredBlock; }

    public RoomType getPreferredRoomType() { return preferredRoomType; }
    public void setPreferredRoomType(RoomType preferredRoomType) { this.preferredRoomType = preferredRoomType; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
}
