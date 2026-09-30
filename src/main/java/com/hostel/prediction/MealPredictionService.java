package com.hostel.prediction;

import com.hostel.model.MealPrediction;
import com.hostel.model.Room;
import com.hostel.model.enums.MealType;
import com.hostel.repository.MealAttendanceRepository;
import com.hostel.repository.MealPredictionRepository;
import com.hostel.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MealPredictionService {

    private final MealAttendanceRepository mealAttendanceRepository;
    private final RoomRepository roomRepository;
    private final MealPredictionRepository mealPredictionRepository;

    private static final double DEFAULT_SAFETY_BUFFER_PERCENT = 5.0; // Configurable 5% default safety buffer

    public MealPredictionService(MealAttendanceRepository mealAttendanceRepository, RoomRepository roomRepository, MealPredictionRepository mealPredictionRepository) {
        this.mealAttendanceRepository = mealAttendanceRepository;
        this.roomRepository = roomRepository;
        this.mealPredictionRepository = mealPredictionRepository;
    }

    public static class PredictionResult {
        private final boolean sufficientData;
        private final int predictedAttendance;
        private final double safetyBufferPercent;
        private final int recommendedPreparation;
        private final double confidenceScore;
        private final String algorithmMethod;
        private final String message;
        private final MealPrediction predictionRecord;
        private final int averageAttendance;
        private final int highestAttendance;
        private final int lowestAttendance;
        private final int historicalRecordCount;

        public PredictionResult(boolean sufficientData, int predictedAttendance, double safetyBufferPercent, int recommendedPreparation, double confidenceScore, String algorithmMethod, String message, MealPrediction record, int averageAttendance, int highestAttendance, int lowestAttendance, int historicalRecordCount) {
            this.sufficientData = sufficientData;
            this.predictedAttendance = predictedAttendance;
            this.safetyBufferPercent = safetyBufferPercent;
            this.recommendedPreparation = recommendedPreparation;
            this.confidenceScore = confidenceScore;
            this.algorithmMethod = algorithmMethod;
            this.message = message;
            this.predictionRecord = record;
            this.averageAttendance = averageAttendance;
            this.highestAttendance = highestAttendance;
            this.lowestAttendance = lowestAttendance;
            this.historicalRecordCount = historicalRecordCount;
        }

        public boolean isSufficientData() { return sufficientData; }
        public int getPredictedAttendance() { return predictedAttendance; }
        public double getSafetyBufferPercent() { return safetyBufferPercent; }
        public int getRecommendedPreparation() { return recommendedPreparation; }
        public double getConfidenceScore() { return confidenceScore; }
        public String getAlgorithmMethod() { return algorithmMethod; }
        public String getMessage() { return message; }
        public MealPrediction getPredictionRecord() { return predictionRecord; }
        public int getAverageAttendance() { return averageAttendance; }
        public int getHighestAttendance() { return highestAttendance; }
        public int getLowestAttendance() { return lowestAttendance; }
        public int getHistoricalRecordCount() { return historicalRecordCount; }
    }

    @Transactional
    public PredictionResult predictMealDemand(LocalDate targetDate, MealType mealType) {
        return predictMealDemandWithBuffer(targetDate, mealType, DEFAULT_SAFETY_BUFFER_PERCENT);
    }

    @Transactional
    public PredictionResult predictMealDemandWithBuffer(LocalDate targetDate, MealType mealType, double bufferPercent) {
        List<Object[]> rawCounts = mealAttendanceRepository.findHistoricalAttendanceCounts(mealType);

        List<Integer> historicalCounts = new ArrayList<>();
        for (Object[] row : rawCounts) {
            if (row.length > 1 && row[1] instanceof Number num) {
                historicalCounts.add(num.intValue());
            }
        }

        // Insufficient Data Check (< 3 historical records)
        if (historicalCounts.size() < 3) {
            int currentSampleCount = historicalCounts.size();
            int currentAvg = currentSampleCount > 0 ? (int) Math.round(historicalCounts.stream().mapToInt(Integer::intValue).average().orElse(0)) : 0;
            return new PredictionResult(
                false,
                currentAvg,
                bufferPercent,
                (int) Math.round(currentAvg * (1.0 + bufferPercent / 100.0)),
                0.0,
                "Weighted Historical Moving Average",
                "Not enough historical attendance data to generate a reliable prediction.",
                null,
                currentAvg,
                currentSampleCount > 0 ? Collections.max(historicalCounts) : 0,
                currentSampleCount > 0 ? Collections.min(historicalCounts) : 0,
                currentSampleCount
            );
        }

        List<Room> rooms = roomRepository.findAll();
        int totalOccupancy = rooms.stream().mapToInt(Room::getCurrentOccupancy).sum();
        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();

        // Weighted moving average: recent days have higher weights
        double weightedSum = 0.0;
        double weightTotal = 0.0;
        double[] weights = { 0.40, 0.25, 0.15, 0.10, 0.05, 0.03, 0.02 };

        for (int i = 0; i < historicalCounts.size() && i < weights.length; i++) {
            weightedSum += historicalCounts.get(i) * weights[i];
            weightTotal += weights[i];
        }

        double rawPrediction = weightedSum / weightTotal;
        double occupancyFactor = (totalCapacity > 0) ? (double) totalOccupancy / totalCapacity : 1.0;
        if (occupancyFactor > 0) {
            rawPrediction = rawPrediction * (0.8 + 0.2 * occupancyFactor);
        }

        int finalPredictedCount = (int) Math.round(rawPrediction);
        if (totalOccupancy > 0) {
            finalPredictedCount = Math.min(finalPredictedCount, totalOccupancy);
        }

        // Calculate Safety Buffer (5% default)
        int recommendedServings = (int) Math.round(finalPredictedCount * (1.0 + bufferPercent / 100.0));

        int avgCount = (int) Math.round(historicalCounts.stream().mapToInt(Integer::intValue).average().orElse(0));
        int maxCount = Collections.max(historicalCounts);
        int minCount = Collections.min(historicalCounts);

        double confidenceScore = Math.min(95.0, 75.0 + (historicalCounts.size() * 3.0));

        MealPrediction prediction = mealPredictionRepository.findByPredictionDateAndMealType(targetDate, mealType)
                .orElse(new MealPrediction());

        prediction.setPredictionDate(targetDate);
        prediction.setMealType(mealType);
        prediction.setTotalOccupancy(totalOccupancy);
        prediction.setPredictedAttendance(finalPredictedCount);
        prediction.setAlgorithmMethod("Weighted Historical Moving Average (+ " + String.format("%.1f", bufferPercent) + "% Buffer)");
        prediction.setConfidenceScore(confidenceScore);
        prediction.setNotes("Based on " + historicalCounts.size() + " historical records. Recommended servings: " + recommendedServings);

        prediction = mealPredictionRepository.save(prediction);

        return new PredictionResult(
            true,
            finalPredictedCount,
            bufferPercent,
            recommendedServings,
            confidenceScore,
            "Weighted Historical Moving Average",
            "Based on " + historicalCounts.size() + " days of historical attendance data.",
            prediction,
            avgCount,
            maxCount,
            minCount,
            historicalCounts.size()
        );
    }
}
