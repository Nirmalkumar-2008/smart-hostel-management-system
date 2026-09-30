package com.hostel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "student_code", nullable = false, unique = true, length = 50)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(nullable = false, length = 100)
    private String course;

    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy = 1;

    @Column(name = "emergency_contact", nullable = false, length = 20)
    private String emergencyContact;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "preferred_block", length = 100)
    private String preferredBlock;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_room_type", length = 50)
    private com.hostel.model.enums.RoomType preferredRoomType;

    @Column(name = "preferred_floor")
    private Integer preferredFloor;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Student() {}

    public Student(Integer id, Integer userId, String studentCode, String fullName, String email, String phone, String gender, String course, Integer yearOfStudy, String emergencyContact, String status) {
        this.id = id;
        this.userId = userId;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.course = course;
        this.yearOfStudy = yearOfStudy;
        this.emergencyContact = emergencyContact;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPreferredBlock() { return preferredBlock; }
    public void setPreferredBlock(String preferredBlock) { this.preferredBlock = preferredBlock; }

    public com.hostel.model.enums.RoomType getPreferredRoomType() { return preferredRoomType; }
    public void setPreferredRoomType(com.hostel.model.enums.RoomType preferredRoomType) { this.preferredRoomType = preferredRoomType; }

    public Integer getPreferredFloor() { return preferredFloor; }
    public void setPreferredFloor(Integer preferredFloor) { this.preferredFloor = preferredFloor; }
}
