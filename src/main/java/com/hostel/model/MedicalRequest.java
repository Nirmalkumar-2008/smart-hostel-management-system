package com.hostel.model;

import com.hostel.model.enums.MedicalStatus;
import com.hostel.model.enums.MedicalUrgency;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_requests")
public class MedicalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "request_type", nullable = false, length = 100)
    private String requestType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalUrgency urgency = MedicalUrgency.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalStatus status = MedicalStatus.PENDING;

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(name = "doctor_assigned", length = 100)
    private String doctorAssigned;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String studentName;
    @Transient
    private String studentCode;

    public MedicalRequest() {}

    public MedicalRequest(Integer id, Integer studentId, String requestType, String description, MedicalUrgency urgency, MedicalStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.requestType = requestType;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MedicalUrgency getUrgency() { return urgency; }
    public void setUrgency(MedicalUrgency urgency) { this.urgency = urgency; }

    public MedicalStatus getStatus() { return status; }
    public void setStatus(MedicalStatus status) { this.status = status; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public String getDoctorAssigned() { return doctorAssigned; }
    public void setDoctorAssigned(String doctorAssigned) { this.doctorAssigned = doctorAssigned; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
}
