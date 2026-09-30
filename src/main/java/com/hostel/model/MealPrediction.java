package com.hostel.model;

import com.hostel.model.enums.MealType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_predictions", uniqueConstraints = {
    @UniqueConstraint(name = "unq_prediction", columnNames = {"prediction_date", "meal_type"})
})
public class MealPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "total_occupancy", nullable = false)
    private Integer totalOccupancy;

    @Column(name = "predicted_attendance", nullable = false)
    private Integer predictedAttendance;

    @Column(name = "algorithm_method", nullable = false, length = 50)
    private String algorithmMethod = "Weighted Historical Moving Average";

    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore = 85.00;

    @Column(length = 255)
    private String notes;

    @Column(name = "generated_at", insertable = false, updatable = false)
    private LocalDateTime generatedAt;

    public MealPrediction() {}

    public MealPrediction(Integer id, LocalDate predictionDate, MealType mealType, Integer totalOccupancy, Integer predictedAttendance, String algorithmMethod, Double confidenceScore, String notes) {
        this.id = id;
        this.predictionDate = predictionDate;
        this.mealType = mealType;
        this.totalOccupancy = totalOccupancy;
        this.predictedAttendance = predictedAttendance;
        this.algorithmMethod = algorithmMethod;
        this.confidenceScore = confidenceScore;
        this.notes = notes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDate predictionDate) { this.predictionDate = predictionDate; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public Integer getTotalOccupancy() { return totalOccupancy; }
    public void setTotalOccupancy(Integer totalOccupancy) { this.totalOccupancy = totalOccupancy; }

    public Integer getPredictedAttendance() { return predictedAttendance; }
    public void setPredictedAttendance(Integer predictedAttendance) { this.predictedAttendance = predictedAttendance; }

    public String getAlgorithmMethod() { return algorithmMethod; }
    public void setAlgorithmMethod(String algorithmMethod) { this.algorithmMethod = algorithmMethod; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
