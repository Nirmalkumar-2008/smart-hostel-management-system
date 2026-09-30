package com.hostel.model;

import com.hostel.model.enums.LostFoundCategory;
import com.hostel.model.enums.LostItemStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_items")
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LostFoundCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String color;

    @Column(length = 50)
    private String brand;

    @Column(name = "last_known_location", nullable = false, length = 100)
    private String lastKnownLocation;

    @Column(name = "lost_date", nullable = false)
    private LocalDate lostDate;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LostItemStatus status = LostItemStatus.OPEN;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;

    public LostItem() {}

    public LostItem(Integer id, Integer studentId, String itemName, LostFoundCategory category, String description, String color, String brand, String lastKnownLocation, LocalDate lostDate, String contactPhone, LostItemStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.color = color;
        this.brand = brand;
        this.lastKnownLocation = lastKnownLocation;
        this.lostDate = lostDate;
        this.contactPhone = contactPhone;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public LostFoundCategory getCategory() { return category; }
    public void setCategory(LostFoundCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLastKnownLocation() { return lastKnownLocation; }
    public void setLastKnownLocation(String lastKnownLocation) { this.lastKnownLocation = lastKnownLocation; }

    public LocalDate getLostDate() { return lostDate; }
    public void setLostDate(LocalDate lostDate) { this.lostDate = lostDate; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public LostItemStatus getStatus() { return status; }
    public void setStatus(LostItemStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
