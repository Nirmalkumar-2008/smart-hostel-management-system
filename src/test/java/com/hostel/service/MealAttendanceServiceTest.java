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
import com.hostel.repository.MealPredictionRepository;
import com.hostel.repository.MessMenuRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MealAttendanceServiceTest {

    private MessMenuRepository messMenuRepository;
    private MealAttendanceRepository mealAttendanceRepository;
    private StudentRepository studentRepository;
    private RoomRepository roomRepository;
    private MealPredictionRepository mealPredictionRepository;
    private MealPredictionService mealPredictionService;
    private MessTimingConfig messTimingConfig;
    private MessService messService;

    @BeforeEach
    void setUp() {
        messMenuRepository = mock(MessMenuRepository.class);
        mealAttendanceRepository = mock(MealAttendanceRepository.class);
        studentRepository = mock(StudentRepository.class);
        roomRepository = mock(RoomRepository.class);
        mealPredictionRepository = mock(MealPredictionRepository.class);
        messTimingConfig = mock(MessTimingConfig.class);

        mealPredictionService = new MealPredictionService(mealAttendanceRepository, roomRepository, mealPredictionRepository);
        messService = new MessService(messMenuRepository, mealAttendanceRepository, studentRepository, mealPredictionService, messTimingConfig);
    }

    @Test
    @DisplayName("1. Student post-meal attendance recorded successfully when window is open")
    void testRecordPostMealAttendance_Success() throws Exception {
        int studentId = 101;
        MealType mealType = MealType.BREAKFAST;
        LocalDate today = LocalDate.now();

        when(messTimingConfig.isWindowOpen(eq(mealType), any(LocalTime.class))).thenReturn(true);
        when(mealAttendanceRepository.existsByStudentIdAndMealDateAndMealType(studentId, today, mealType)).thenReturn(false);

        MessMenu menu = new MessMenu(1, today, mealType, "07:00 AM", "10:00 AM", "Delicious", MessMenuStatus.PUBLISHED, List.of("Idli"));
        when(messMenuRepository.findByMenuDateAndMealType(today, mealType)).thenReturn(Optional.of(menu));

        MealAttendance expected = new MealAttendance(1, studentId, 1, today, mealType, MealAttendanceStatus.YES, "08:42 AM", null);
        when(mealAttendanceRepository.save(any(MealAttendance.class))).thenReturn(expected);

        MealAttendance result = messService.recordPostMealAttendance(studentId, mealType);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals(MealType.BREAKFAST, result.getMealType());
        verify(mealAttendanceRepository, times(1)).save(any(MealAttendance.class));
    }

    @Test
    @DisplayName("2. Duplicate attendance rejected for same student, date, and meal")
    void testRecordPostMealAttendance_DuplicateRejected() {
        int studentId = 101;
        MealType mealType = MealType.LUNCH;
        LocalDate today = LocalDate.now();

        when(messTimingConfig.isWindowOpen(eq(mealType), any(LocalTime.class))).thenReturn(true);
        when(mealAttendanceRepository.existsByStudentIdAndMealDateAndMealType(studentId, today, mealType)).thenReturn(true);

        HostelException ex = assertThrows(HostelException.class, () -> messService.recordPostMealAttendance(studentId, mealType));
        assertEquals("Meal attendance has already been recorded for this meal.", ex.getMessage());
        verify(mealAttendanceRepository, never()).save(any(MealAttendance.class));
    }

    @Test
    @DisplayName("3. Attendance outside configured time window rejected")
    void testRecordPostMealAttendance_WindowClosedRejected() {
        int studentId = 101;
        MealType mealType = MealType.DINNER;

        when(messTimingConfig.isWindowOpen(eq(mealType), any(LocalTime.class))).thenReturn(false);

        HostelException ex = assertThrows(HostelException.class, () -> messService.recordPostMealAttendance(studentId, mealType));
        assertEquals("Meal attendance is currently closed.", ex.getMessage());
        verify(mealAttendanceRepository, never()).save(any(MealAttendance.class));
    }

    @Test
    @DisplayName("4. Prediction calculation & 5% Safety Buffer Math")
    void testPredictionWithSafetyBuffer() {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        MealType mealType = MealType.LUNCH;

        List<Object[]> mockCounts = new ArrayList<>();
        mockCounts.add(new Object[]{LocalDate.now().minusDays(1), 460});
        mockCounts.add(new Object[]{LocalDate.now().minusDays(2), 462});
        mockCounts.add(new Object[]{LocalDate.now().minusDays(3), 455});
        mockCounts.add(new Object[]{LocalDate.now().minusDays(4), 470});
        mockCounts.add(new Object[]{LocalDate.now().minusDays(5), 458});

        when(mealAttendanceRepository.findHistoricalAttendanceCounts(mealType)).thenReturn(mockCounts);
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        MealPredictionService.PredictionResult result = mealPredictionService.predictMealDemandWithBuffer(targetDate, mealType, 5.0);

        assertTrue(result.isSufficientData());
        assertTrue(result.getPredictedAttendance() > 0);
        assertEquals(5.0, result.getSafetyBufferPercent());
        // Verify math: Recommended Preparation = Math.round(predicted * 1.05)
        int expectedRecommended = (int) Math.round(result.getPredictedAttendance() * 1.05);
        assertEquals(expectedRecommended, result.getRecommendedPreparation());
    }

    @Test
    @DisplayName("5. Insufficient historical data handled (< 3 records)")
    void testPredictionInsufficientData() {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        MealType mealType = MealType.BREAKFAST;

        List<Object[]> mockCounts = new ArrayList<>();
        mockCounts.add(new Object[]{LocalDate.now().minusDays(1), 100});

        when(mealAttendanceRepository.findHistoricalAttendanceCounts(mealType)).thenReturn(mockCounts);

        MealPredictionService.PredictionResult result = mealPredictionService.predictMealDemand(targetDate, mealType);

        assertFalse(result.isSufficientData());
        assertEquals("Not enough historical attendance data to generate a reliable prediction.", result.getMessage());
    }
}
