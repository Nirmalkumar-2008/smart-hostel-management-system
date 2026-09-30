package com.hostel.prediction;

import com.hostel.model.enums.MealType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MealPredictionEngine {

    private final MealPredictionService mealPredictionService;

    public MealPredictionEngine(MealPredictionService mealPredictionService) {
        this.mealPredictionService = mealPredictionService;
    }

    public MealPredictionService.PredictionResult predictMealDemand(LocalDate targetDate, MealType mealType) {
        return mealPredictionService.predictMealDemand(targetDate, mealType);
    }
}
