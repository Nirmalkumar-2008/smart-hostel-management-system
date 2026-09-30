package com.hostel.config;

import com.hostel.model.enums.MealType;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class MessTimingConfig {

    public static class TimeWindow {
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final String displayWindow;

        public TimeWindow(LocalTime startTime, LocalTime endTime, String displayWindow) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.displayWindow = displayWindow;
        }

        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public String getDisplayWindow() { return displayWindow; }

        public boolean isOpen(LocalTime time) {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        }
    }

    private TimeWindow breakfastWindow = new TimeWindow(LocalTime.of(7, 0), LocalTime.of(10, 0), "07:00 AM - 10:00 AM");
    private TimeWindow lunchWindow = new TimeWindow(LocalTime.of(12, 0), LocalTime.of(15, 0), "12:00 PM - 03:00 PM");
    private TimeWindow dinnerWindow = new TimeWindow(LocalTime.of(19, 0), LocalTime.of(22, 0), "07:00 PM - 10:00 PM");

    public TimeWindow getWindow(MealType mealType) {
        if (mealType == null) return breakfastWindow;
        return switch (mealType) {
            case BREAKFAST -> breakfastWindow;
            case LUNCH -> lunchWindow;
            case DINNER -> dinnerWindow;
        };
    }

    public boolean isWindowOpen(MealType mealType, LocalTime time) {
        return getWindow(mealType).isOpen(time);
    }

    public String getTimeWindowString(MealType mealType) {
        return getWindow(mealType).getDisplayWindow();
    }
}
