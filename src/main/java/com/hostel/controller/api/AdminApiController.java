package com.hostel.controller.api;

import com.hostel.algorithm.RoomRecommendationEngine;
import com.hostel.algorithm.RoomRecommendationService;
import com.hostel.model.FoodWasteRecord;
import com.hostel.model.Room;
import com.hostel.model.enums.MealType;
import com.hostel.model.enums.RoomType;
import com.hostel.prediction.MealPredictionService;
import com.hostel.service.AnalyticsService;
import com.hostel.service.FoodWasteService;
import com.hostel.service.MessService;
import com.hostel.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AnalyticsService analyticsService;
    private final FoodWasteService foodWasteService;
    private final MessService messService;
    private final RoomService roomService;
    private final RoomRecommendationService recommendationService;

    public AdminApiController(AnalyticsService analyticsService, FoodWasteService foodWasteService, MessService messService, RoomService roomService, RoomRecommendationService recommendationService) {
        this.analyticsService = analyticsService;
        this.foodWasteService = foodWasteService;
        this.messService = messService;
        this.roomService = roomService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AnalyticsService.SystemDashboardStats> getStats() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/food-waste/chart-data")
    public ResponseEntity<List<FoodWasteRecord>> getFoodWasteChartData() {
        return ResponseEntity.ok(foodWasteService.getAllFoodWasteRecords());
    }

    @GetMapping("/mess/predict")
    public ResponseEntity<MealPredictionService.PredictionResult> predictMealDemand(@RequestParam(required = false) String date, @RequestParam(required = false) MealType mealType) {
        LocalDate targetDate = (date != null && !date.trim().isEmpty()) ? LocalDate.parse(date) : LocalDate.now().plusDays(1);
        MealType targetMeal = (mealType != null) ? mealType : MealType.DINNER;
        return ResponseEntity.ok(messService.generateMealPrediction(targetDate, targetMeal));
    }

    @GetMapping("/rooms/recommendations")
    public ResponseEntity<List<RoomRecommendationEngine.RecommendationResult>> getRecommendations(@RequestParam(required = false) String preferredBlock, @RequestParam(required = false) Integer preferredFloor, @RequestParam(required = false) RoomType preferredRoomType) {
        List<Room> availableRooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(recommendationService.rankRooms(availableRooms, preferredBlock, preferredFloor, preferredRoomType));
    }
}
