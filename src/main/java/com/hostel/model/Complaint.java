package com.hostel.model;

import com.hostel.model.enums.ComplaintCategory;
import com.hostel.model.enums.ComplaintPriority;
import com.hostel.model.enums.ComplaintStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "affected_students_count", nullable = false)
    private Integer affectedStudentsCount = 1;

    @Column(name = "severity_score", nullable = false)
    private Integer severityScore = 1; // 1-10

    @Enumerated(EnumType.STRING)
    @Column(name = "calculated_priority", nullable = false)
    private ComplaintPriority calculatedPriority = ComplaintPriority.LOW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;
    @Transient
    private String studentCode;

    public Complaint() {}

    public Complaint(Integer id, Integer studentId, ComplaintCategory category, String title, String description, String location, Integer affectedStudentsCount, Integer severityScore, ComplaintPriority calculatedPriority, ComplaintStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
        this.affectedStudentsCount = affectedStudentsCount;
        this.severityScore = severityScore;
        this.calculatedPriority = calculatedPriority;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getAffectedStudentsCount() { return affectedStudentsCount; }
    public void setAffectedStudentsCount(Integer affectedStudentsCount) { this.affectedStudentsCount = affectedStudentsCount; }

    public Integer getSeverityScore() { return severityScore; }
    public void setSeverityScore(Integer severityScore) { this.severityScore = severityScore; }

    public ComplaintPriority getCalculatedPriority() { return calculatedPriority; }
    public void setCalculatedPriority(ComplaintPriority calculatedPriority) { this.calculatedPriority = calculatedPriority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
}
