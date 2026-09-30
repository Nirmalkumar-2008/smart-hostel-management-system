package com.hostel.service;

import com.hostel.config.MessTimingConfig;
import com.hostel.exception.HostelException;
import com.hostel.model.MealAttendance;
import com.hostel.model.MessMenu;
import com.hostel.model.enums.MealAttendanceStatus;
import com.hostel.model.enums.MealType;
import com.hostel.model.enums.MessMenuStatus;
import com.hostel.prediction.MealPredictionService;
import com.hostel.repository.MealAttendanceRepository;
import com.hostel.repository.MessMenuRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessService {

    private final MessMenuRepository messMenuRepository;
    private final MealAttendanceRepository mealAttendanceRepository;
    private final StudentRepository studentRepository;
    private final MealPredictionService mealPredictionService;
    private final MessTimingConfig messTimingConfig;

    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    public MessService(MessMenuRepository messMenuRepository,
                       MealAttendanceRepository mealAttendanceRepository,
                       StudentRepository studentRepository,
                       MealPredictionService mealPredictionService,
                       MessTimingConfig messTimingConfig) {
        this.messMenuRepository = messMenuRepository;
        this.mealAttendanceRepository = mealAttendanceRepository;
        this.studentRepository = studentRepository;
        this.mealPredictionService = mealPredictionService;
        this.messTimingConfig = messTimingConfig;
    }

    // ==========================================
    // POST-MEAL ATTENDANCE RECORDING
    // ==========================================

    @Transactional
    public MealAttendance recordPostMealAttendance(int studentId, MealType mealType) throws HostelException {
        if (mealType == null) throw new HostelException("Meal type is required.");

        LocalDate today = LocalDate.now(INDIA_ZONE);
        LocalTime now = LocalTime.now(INDIA_ZONE);

        // 1. Time Window Validation
        if (!messTimingConfig.isWindowOpen(mealType, now)) {
            throw new HostelException("Meal attendance is currently closed.");
        }

        // 2. Duplicate Check Validation
        if (mealAttendanceRepository.existsByStudentIdAndMealDateAndMealType(studentId, today, mealType)) {
            throw new HostelException("Meal attendance has already been recorded for this meal.");
        }

        // 3. Find Menu for Today
        Optional<MessMenu> menuOpt = messMenuRepository.findByMenuDateAndMealType(today, mealType);
        Integer menuId = menuOpt.map(MessMenu::getId).orElse(null);

        String timeString = now.format(TIME_FORMATTER);

        MealAttendance attendance = new MealAttendance(
                null,
                studentId,
                menuId,
                today,
                mealType,
                MealAttendanceStatus.YES,
                timeString,
                java.time.LocalDateTime.now(INDIA_ZONE)
        );

        return mealAttendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<MealAttendance> getStudentAttendanceHistory(int studentId) {
        List<MealAttendance> list = mealAttendanceRepository.findByStudentIdOrderByMealDateDescMealTypeAsc(studentId);
        for (MealAttendance ma : list) {
            if (ma.getMenuId() != null) {
                messMenuRepository.findById(ma.getMenuId()).ifPresent(m -> {
                    if (m.getItems() != null && !m.getItems().isEmpty()) {
                        ma.setMenuItemsSummary(String.join(", ", m.getItems()));
                    }
                });
            }
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Map<MealType, MealAttendance> getStudentTodayAttendanceMap(int studentId) {
        LocalDate today = LocalDate.now(INDIA_ZONE);
        List<MealAttendance> todayList = mealAttendanceRepository.findByStudentIdAndMealDate(studentId, today);

        Map<MealType, MealAttendance> map = new HashMap<>();
        for (MealAttendance ma : todayList) {
            map.put(ma.getMealType(), ma);
        }
        return map;
    }

    @Transactional(readOnly = true)
    public List<MealAttendance> getFilteredAttendance(LocalDate date, MealType mealType) {
        List<MealAttendance> list = mealAttendanceRepository.findWithFilters(date, mealType);
        for (MealAttendance ma : list) {
            studentRepository.findById(ma.getStudentId()).ifPresent(s -> {
                ma.setStudentName(s.getFullName());
                ma.setStudentCode(s.getStudentCode());
            });
            if (ma.getMenuId() != null) {
                messMenuRepository.findById(ma.getMenuId()).ifPresent(m -> {
                    if (m.getItems() != null && !m.getItems().isEmpty()) {
                        ma.setMenuItemsSummary(String.join(", ", m.getItems()));
                    }
                });
            }
        }
        return list;
    }

    // ==========================================
    // SMART MESS MENU MANAGEMENT (CRUD & VALIDATION)
    // ==========================================

    @Transactional
    public MessMenu createMenu(LocalDate menuDate, MealType mealType, String startTime, String endTime, List<String> items, String description, MessMenuStatus status) throws HostelException {
        if (menuDate == null) throw new HostelException("Menu date is required.");
        if (mealType == null) throw new HostelException("Meal type is required.");
        if (startTime == null || startTime.trim().isEmpty()) throw new HostelException("Start time is required.");
        if (endTime == null || endTime.trim().isEmpty()) throw new HostelException("End time is required.");

        List<String> cleanItems = (items != null) ? items.stream()
                .filter(i -> i != null && !i.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList()) : new ArrayList<>();

        if (cleanItems.isEmpty()) {
            throw new HostelException("At least one food item is required.");
        }

        // Duplicate Check
        Optional<MessMenu> existing = messMenuRepository.findByMenuDateAndMealType(menuDate, mealType);
        if (existing.isPresent()) {
            throw new HostelException(mealType.name() + " menu already exists for " + menuDate + ".");
        }

        MessMenu menu = new MessMenu(null, menuDate, mealType, startTime.trim(), endTime.trim(), (description != null ? description.trim() : null), (status != null ? status : MessMenuStatus.DRAFT), cleanItems);
        return messMenuRepository.save(menu);
    }

    @Transactional
    public MessMenu updateMenu(Integer id, LocalDate menuDate, MealType mealType, String startTime, String endTime, List<String> items, String description, MessMenuStatus status) throws HostelException {
        MessMenu menu = messMenuRepository.findById(id)
                .orElseThrow(() -> new HostelException("Mess menu with ID " + id + " not found."));

        if (menuDate == null) throw new HostelException("Menu date is required.");
        if (mealType == null) throw new HostelException("Meal type is required.");
        if (startTime == null || startTime.trim().isEmpty()) throw new HostelException("Start time is required.");
        if (endTime == null || endTime.trim().isEmpty()) throw new HostelException("End time is required.");

        List<String> cleanItems = (items != null) ? items.stream()
                .filter(i -> i != null && !i.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList()) : new ArrayList<>();

        if (cleanItems.isEmpty()) {
            throw new HostelException("At least one food item is required.");
        }

        // Duplicate Check
        Optional<MessMenu> existing = messMenuRepository.findByMenuDateAndMealType(menuDate, mealType);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new HostelException(mealType.name() + " menu already exists for " + menuDate + ".");
        }

        menu.setMenuDate(menuDate);
        menu.setMealType(mealType);
        menu.setStartTime(startTime.trim());
        menu.setEndTime(endTime.trim());
        menu.setItems(cleanItems);
        menu.setDescription(description != null ? description.trim() : null);
        menu.setStatus(status != null ? status : MessMenuStatus.DRAFT);

        return messMenuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Integer id) throws HostelException {
        if (!messMenuRepository.existsById(id)) {
            throw new HostelException("Mess menu with ID " + id + " not found.");
        }
        messMenuRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public MessMenu getMenuById(Integer id) throws HostelException {
        return messMenuRepository.findById(id)
                .orElseThrow(() -> new HostelException("Mess menu with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<MessMenu> getTodayPublishedMenus() {
        return messMenuRepository.findByMenuDateAndStatusOrderByMealTypeAsc(LocalDate.now(INDIA_ZONE), MessMenuStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public Map<String, List<MessMenu>> getWeeklyPublishedMenusMap() {
        LocalDate monday = LocalDate.now(INDIA_ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        List<MessMenu> weeklyList = messMenuRepository.findByMenuDateBetweenAndStatusOrderByMenuDateAscMealTypeAsc(monday, sunday, MessMenuStatus.PUBLISHED);

        Map<String, List<MessMenu>> map = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            String dayName = date.getDayOfWeek().name();
            List<MessMenu> dayMenus = weeklyList.stream()
                    .filter(m -> m.getMenuDate().equals(date))
                    .collect(Collectors.toList());
            map.put(dayName + " (" + date + ")", dayMenus);
        }
        return map;
    }

    @Transactional(readOnly = true)
    public List<MessMenu> filterMenus(LocalDate date, MealType mealType, MessMenuStatus status) {
        return messMenuRepository.findWithFilters(date, mealType, status);
    }

    // Dashboard Statistics & Analytics
    @Transactional(readOnly = true)
    public long getTodayMealCount() {
        return messMenuRepository.countByMenuDate(LocalDate.now(INDIA_ZONE));
    }

    @Transactional(readOnly = true)
    public long getPublishedMenuCount() {
        return messMenuRepository.countByStatus(MessMenuStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public long getUpcomingMenuCount() {
        return messMenuRepository.countByMenuDateAfter(LocalDate.now(INDIA_ZONE));
    }

    @Transactional(readOnly = true)
    public String getTodayStatusText() {
        long publishedCount = messMenuRepository.countByMenuDateAndStatus(LocalDate.now(INDIA_ZONE), MessMenuStatus.PUBLISHED);
        if (publishedCount > 0) return "Published";
        long totalCount = messMenuRepository.countByMenuDate(LocalDate.now(INDIA_ZONE));
        if (totalCount > 0) return "Draft Only";
        return "Not Published";
    }

    @Transactional(readOnly = true)
    public long getTotalStudentsCount() {
        return studentRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTodayAttendanceCount(MealType mealType) {
        LocalDate today = LocalDate.now(INDIA_ZONE);
        if (mealType != null) {
            return mealAttendanceRepository.countByMealDateAndMealTypeAndAttendance(today, mealType, MealAttendanceStatus.YES);
        }
        return mealAttendanceRepository.countByMealDateAndAttendance(today, MealAttendanceStatus.YES);
    }

    @Transactional(readOnly = true)
    public double calculateAttendancePercentage(long attendanceCount, long totalStudents) {
        if (totalStudents <= 0) return 0.0;
        return ((double) attendanceCount / totalStudents) * 100.0;
    }

    @Transactional
    public MealPredictionService.PredictionResult generateMealPrediction(LocalDate targetDate, MealType mealType) {
        return mealPredictionService.predictMealDemand(targetDate, mealType);
    }

    public MessTimingConfig getMessTimingConfig() {
        return messTimingConfig;
    }
}
