package com.hostel.repository;

import com.hostel.model.MealAttendance;
import com.hostel.model.enums.MealAttendanceStatus;
import com.hostel.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealAttendanceRepository extends JpaRepository<MealAttendance, Integer> {

    Optional<MealAttendance> findByStudentIdAndMealDateAndMealType(Integer studentId, LocalDate mealDate, MealType mealType);

    boolean existsByStudentIdAndMealDateAndMealType(Integer studentId, LocalDate mealDate, MealType mealType);

    List<MealAttendance> findByStudentIdOrderByMealDateDescMealTypeAsc(Integer studentId);

    List<MealAttendance> findByStudentIdAndMealDate(Integer studentId, LocalDate mealDate);

    List<MealAttendance> findByMealDateAndMealType(LocalDate mealDate, MealType mealType);

    List<MealAttendance> findByMealDate(LocalDate mealDate);

    @Query("SELECT m FROM MealAttendance m WHERE (:date IS NULL OR m.mealDate = :date) AND (:mealType IS NULL OR m.mealType = :mealType) ORDER BY m.mealDate DESC, m.recordedAt DESC")
    List<MealAttendance> findWithFilters(@Param("date") LocalDate date, @Param("mealType") MealType mealType);

    long countByMealDateAndMealTypeAndAttendance(LocalDate mealDate, MealType mealType, MealAttendanceStatus attendance);

    long countByMealDateAndAttendance(LocalDate mealDate, MealAttendanceStatus attendance);

    @Query("SELECT COUNT(m) FROM MealAttendance m WHERE m.mealDate = :mealDate AND m.mealType = :mealType AND m.attendance = :status")
    long countAttendanceByDateAndTypeAndStatus(@Param("mealDate") LocalDate mealDate, @Param("mealType") MealType mealType, @Param("status") MealAttendanceStatus status);

    @Query("SELECT m.mealDate AS date, COUNT(m) AS count FROM MealAttendance m WHERE m.mealType = :mealType AND m.attendance = 'YES' GROUP BY m.mealDate ORDER BY m.mealDate DESC")
    List<Object[]> findHistoricalAttendanceCounts(@Param("mealType") MealType mealType);
}
