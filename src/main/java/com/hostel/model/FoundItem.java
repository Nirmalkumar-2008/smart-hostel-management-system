package com.hostel.model;

import com.hostel.model.enums.FoundItemStatus;
import com.hostel.model.enums.LostFoundCategory;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "found_items")
public class FoundItem {

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

    @Column(name = "found_location", nullable = false, length = 100)
    private String foundLocation;

    @Column(name = "found_date", nullable = false)
    private LocalDate foundDate;

    @Column(name = "storage_location", nullable = false, length = 100)
    private String storageLocation = "Warden Office";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoundItemStatus status = FoundItemStatus.OPEN;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;

    public FoundItem() {}

    public FoundItem(Integer id, Integer studentId, String itemName, LostFoundCategory category, String description, String color, String brand, String foundLocation, LocalDate foundDate, String storageLocation, FoundItemStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.color = color;
        this.brand = brand;
        this.foundLocation = foundLocation;
        this.foundDate = foundDate;
        this.storageLocation = storageLocation;
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

    public String getFoundLocation() { return foundLocation; }
    public void setFoundLocation(String foundLocation) { this.foundLocation = foundLocation; }

    public LocalDate getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDate foundDate) { this.foundDate = foundDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public FoundItemStatus getStatus() { return status; }
    public void setStatus(FoundItemStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
