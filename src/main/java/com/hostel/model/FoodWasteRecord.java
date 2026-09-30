package com.hostel.model;

import com.hostel.model.enums.MealType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_waste_records", uniqueConstraints = {
    @UniqueConstraint(name = "unq_waste_record", columnNames = {"record_date", "meal_type"})
})
public class FoodWasteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "food_prepared_headcount", nullable = false)
    private Integer foodPreparedHeadcount;

    @Column(name = "expected_attendance", nullable = false)
    private Integer expectedAttendance;

    @Column(name = "actual_attendance", nullable = false)
    private Integer actualAttendance;

    @Column(name = "remaining_food_headcount", nullable = false)
    private Integer remainingFoodHeadcount;

    @Column(name = "estimated_waste_kg", nullable = false)
    private double estimatedWasteKg;

    @Column(name = "waste_percentage", nullable = false)
    private double wastePercentage;

    @Column(length = 255)
    private String notes;

    @Column(name = "recorded_by", nullable = false)
    private Integer recordedBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String recordedByName;

    public FoodWasteRecord() {}

    public FoodWasteRecord(Integer id, LocalDate recordDate, MealType mealType, Integer foodPreparedHeadcount, Integer expectedAttendance, Integer actualAttendance, Integer remainingFoodHeadcount, Double estimatedWasteKg, Double wastePercentage, String notes, Integer recordedBy) {
        this.id = id;
        this.recordDate = recordDate;
        this.mealType = mealType;
        this.foodPreparedHeadcount = foodPreparedHeadcount;
        this.expectedAttendance = expectedAttendance;
        this.actualAttendance = actualAttendance;
        this.remainingFoodHeadcount = remainingFoodHeadcount;
        this.estimatedWasteKg = estimatedWasteKg;
        this.wastePercentage = wastePercentage;
        this.notes = notes;
        this.recordedBy = recordedBy;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public Integer getFoodPreparedHeadcount() { return foodPreparedHeadcount; }
    public void setFoodPreparedHeadcount(Integer foodPreparedHeadcount) { this.foodPreparedHeadcount = foodPreparedHeadcount; }

    public Integer getExpectedAttendance() { return expectedAttendance; }
    public void setExpectedAttendance(Integer expectedAttendance) { this.expectedAttendance = expectedAttendance; }

    public Integer getActualAttendance() { return actualAttendance; }
    public void setActualAttendance(Integer actualAttendance) { this.actualAttendance = actualAttendance; }

    public Integer getRemainingFoodHeadcount() { return remainingFoodHeadcount; }
    public void setRemainingFoodHeadcount(Integer remainingFoodHeadcount) { this.remainingFoodHeadcount = remainingFoodHeadcount; }

    public Double getEstimatedWasteKg() { return estimatedWasteKg; }
    public void setEstimatedWasteKg(Double estimatedWasteKg) { this.estimatedWasteKg = estimatedWasteKg; }

    public Double getWastePercentage() { return wastePercentage; }
    public void setWastePercentage(Double wastePercentage) { this.wastePercentage = wastePercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Integer recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRecordedByName() { return recordedByName; }
    public void setRecordedByName(String recordedByName) { this.recordedByName = recordedByName; }
}
