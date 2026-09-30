package com.hostel.service;

import com.hostel.model.FoodWasteRecord;
import com.hostel.model.enums.MealType;
import com.hostel.repository.AdminRepository;
import com.hostel.repository.FoodWasteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FoodWasteService {

    private final FoodWasteRepository foodWasteRepository;
    private final AdminRepository adminRepository;

    public FoodWasteService(FoodWasteRepository foodWasteRepository, AdminRepository adminRepository) {
        this.foodWasteRepository = foodWasteRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public FoodWasteRecord recordFoodWaste(LocalDate recordDate, MealType mealType, int prepared, int expected, int actual, String notes, int adminId) {
        int remaining = Math.max(0, prepared - actual);
        double wasteKg = remaining * 0.35; // 0.35kg per unconsumed portion
        double wastePercentage = (prepared > 0) ? ((double) remaining / prepared) * 100.0 : 0.0;

        FoodWasteRecord record = foodWasteRepository.findByRecordDateAndMealType(recordDate, mealType)
                .orElse(new FoodWasteRecord());

        record.setRecordDate(recordDate);
        record.setMealType(mealType);
        record.setFoodPreparedHeadcount(prepared);
        record.setExpectedAttendance(expected);
        record.setActualAttendance(actual);
        record.setRemainingFoodHeadcount(remaining);
        record.setEstimatedWasteKg(wasteKg);
        record.setWastePercentage(wastePercentage);
        record.setNotes(notes);
        record.setRecordedBy(adminId);

        return foodWasteRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<FoodWasteRecord> getAllFoodWasteRecords() {
        List<FoodWasteRecord> list = foodWasteRepository.findAllByOrderByRecordDateDesc();
        for (FoodWasteRecord r : list) {
            adminRepository.findById(r.getRecordedBy()).ifPresent(a -> r.setRecordedByName(a.getFullName()));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<FoodWasteRecord> getFoodWasteForDate(LocalDate recordDate) {
        List<FoodWasteRecord> list = foodWasteRepository.findByRecordDate(recordDate);
        for (FoodWasteRecord r : list) {
            adminRepository.findById(r.getRecordedBy()).ifPresent(a -> r.setRecordedByName(a.getFullName()));
        }
        return list;
    }
}
