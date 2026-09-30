package com.hostel.model;

import com.hostel.model.enums.MealAttendanceStatus;
import com.hostel.model.enums.MealType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_attendance", uniqueConstraints = {
    @UniqueConstraint(name = "unq_student_meal", columnNames = {"student_id", "meal_date", "meal_type"})
})
public class MealAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "menu_id")
    private Integer menuId;

    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 30)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MealAttendanceStatus attendance = MealAttendanceStatus.YES;

    @Column(name = "attendance_time", length = 20)
    private String attendanceTime;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Column(name = "marked_at", insertable = false, updatable = false)
    private LocalDateTime markedAt;

    @Transient
    private String studentName;
    @Transient
    private String studentCode;
    @Transient
    private String menuItemsSummary;

    public MealAttendance() {}

    public MealAttendance(Integer id, Integer studentId, Integer menuId, LocalDate mealDate, MealType mealType, MealAttendanceStatus attendance, String attendanceTime, LocalDateTime recordedAt) {
        this.id = id;
        this.studentId = studentId;
        this.menuId = menuId;
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.attendance = attendance;
        this.attendanceTime = attendanceTime;
        this.recordedAt = recordedAt != null ? recordedAt : LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getMenuId() { return menuId; }
    public void setMenuId(Integer menuId) { this.menuId = menuId; }

    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public MealAttendanceStatus getAttendance() { return attendance; }
    public void setAttendance(MealAttendanceStatus attendance) { this.attendance = attendance; }

    public String getAttendanceTime() { return attendanceTime; }
    public void setAttendanceTime(String attendanceTime) { this.attendanceTime = attendanceTime; }

    public LocalDateTime getRecordedAt() { return recordedAt != null ? recordedAt : markedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getMenuItemsSummary() { return menuItemsSummary; }
    public void setMenuItemsSummary(String menuItemsSummary) { this.menuItemsSummary = menuItemsSummary; }
}
