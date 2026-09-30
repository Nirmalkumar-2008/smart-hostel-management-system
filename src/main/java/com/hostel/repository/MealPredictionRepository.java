package com.hostel.repository;

import com.hostel.model.MealPrediction;
import com.hostel.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPredictionRepository extends JpaRepository<MealPrediction, Integer> {
    Optional<MealPrediction> findByPredictionDateAndMealType(LocalDate predictionDate, MealType mealType);
    List<MealPrediction> findByPredictionDate(LocalDate predictionDate);
    List<MealPrediction> findAllByOrderByPredictionDateDesc();
}
