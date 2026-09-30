package com.hostel.repository;

import com.hostel.model.FoodWasteRecord;
import com.hostel.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodWasteRepository extends JpaRepository<FoodWasteRecord, Integer> {
    Optional<FoodWasteRecord> findByRecordDateAndMealType(LocalDate recordDate, MealType mealType);
    List<FoodWasteRecord> findByRecordDate(LocalDate recordDate);
    List<FoodWasteRecord> findAllByOrderByRecordDateDesc();
}
