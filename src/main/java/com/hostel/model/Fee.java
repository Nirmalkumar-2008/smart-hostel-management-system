package com.hostel.model;

import com.hostel.model.enums.FeeStatus;
import com.hostel.model.enums.FeeType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fee_code", nullable = false, unique = true, length = 30)
    private String feeCode;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeStatus status = FeeStatus.UNPAID;

    @Column(name = "academic_term", nullable = false, length = 30)
    private String academicTerm;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private Double totalPaidAmount = 0.0;
    @Transient
    private String studentName;
    @Transient
    private String studentCode;

    public Fee() {}

    public Fee(Integer id, String feeCode, Integer studentId, FeeType feeType, Double totalAmount, LocalDate dueDate, FeeStatus status, String academicTerm) {
        this.id = id;
        this.feeCode = feeCode;
        this.studentId = studentId;
        this.feeType = feeType;
        this.totalAmount = totalAmount;
        this.dueDate = dueDate;
        this.status = status;
        this.academicTerm = academicTerm;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFeeCode() { return feeCode; }
    public void setFeeCode(String feeCode) { this.feeCode = feeCode; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public FeeType getFeeType() { return feeType; }
    public void setFeeType(FeeType feeType) { this.feeType = feeType; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public FeeStatus getStatus() { return status; }
    public void setStatus(FeeStatus status) { this.status = status; }

    public String getAcademicTerm() { return academicTerm; }
    public void setAcademicTerm(String academicTerm) { this.academicTerm = academicTerm; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getTotalPaidAmount() { return totalPaidAmount; }
    public void setTotalPaidAmount(Double totalPaidAmount) { this.totalPaidAmount = totalPaidAmount; }

    public Double getRemainingBalance() {
        return Math.max(0.0, (totalAmount != null ? totalAmount : 0.0) - (totalPaidAmount != null ? totalPaidAmount : 0.0));
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
}
