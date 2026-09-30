package com.hostel.repository;

import com.hostel.model.MessMenu;
import com.hostel.model.enums.MealType;
import com.hostel.model.enums.MessMenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessMenuRepository extends JpaRepository<MessMenu, Integer> {

    Optional<MessMenu> findByMenuDateAndMealType(LocalDate menuDate, MealType mealType);

    List<MessMenu> findByMenuDateAndStatusOrderByMealTypeAsc(LocalDate menuDate, MessMenuStatus status);

    List<MessMenu> findByMenuDateBetweenAndStatusOrderByMenuDateAscMealTypeAsc(LocalDate startDate, LocalDate endDate, MessMenuStatus status);

    List<MessMenu> findAllByOrderByMenuDateDescMealTypeAsc();

    @Query("SELECT m FROM MessMenu m WHERE (:date IS NULL OR m.menuDate = :date) AND (:mealType IS NULL OR m.mealType = :mealType) AND (:status IS NULL OR m.status = :status) ORDER BY m.menuDate DESC, m.mealType ASC")
    List<MessMenu> findWithFilters(@Param("date") LocalDate date, @Param("mealType") MealType mealType, @Param("status") MessMenuStatus status);

    long countByMenuDate(LocalDate menuDate);

    long countByMenuDateAndStatus(LocalDate menuDate, MessMenuStatus status);

    long countByStatus(MessMenuStatus status);

    long countByMenuDateAfter(LocalDate date);
}
