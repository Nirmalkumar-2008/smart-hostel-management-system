-- Smart Hostel Management System Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS hostel_management_db
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE hostel_management_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    password_salt VARCHAR(128) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- 2. Admins Table
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) NOT NULL DEFAULT 'Hostel Administration',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Students Table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    course VARCHAR(100) NOT NULL,
    year_of_study INT NOT NULL DEFAULT 1,
    emergency_contact VARCHAR(20) NOT NULL,
    status ENUM('ACTIVE', 'ALUMNI', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_students_code (student_code)
) ENGINE=InnoDB;

-- 4. Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    block VARCHAR(10) NOT NULL,
    floor INT NOT NULL,
    capacity INT NOT NULL,
    current_occupancy INT NOT NULL DEFAULT 0,
    room_type ENUM('SINGLE', 'DOUBLE', 'TRIPLE', 'DORMITORY') NOT NULL,
    status ENUM('AVAILABLE', 'FULL', 'MAINTENANCE', 'RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    base_monthly_rent DECIMAL(10,2) NOT NULL DEFAULT 5000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_occupancy CHECK (current_occupancy <= capacity AND current_occupancy >= 0),
    INDEX idx_rooms_block_type (block, room_type),
    INDEX idx_rooms_status (status)
) ENGINE=InnoDB;

-- 5. Room Allocations Table
CREATE TABLE IF NOT EXISTS room_allocations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    allocated_date DATE NOT NULL,
    deallocated_date DATE DEFAULT NULL,
    status ENUM('ACTIVE', 'DEALLOCATED') NOT NULL DEFAULT 'ACTIVE',
    notes VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    INDEX idx_allocation_student (student_id),
    INDEX idx_allocation_room (room_id)
) ENGINE=InnoDB;

-- 6. Waiting List Table
CREATE TABLE IF NOT EXISTS waiting_list (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    preferred_block VARCHAR(10) DEFAULT NULL,
    preferred_room_type ENUM('SINGLE', 'DOUBLE', 'TRIPLE', 'DORMITORY') DEFAULT NULL,
    request_date DATE NOT NULL,
    priority_score INT NOT NULL DEFAULT 50,
    status ENUM('WAITING', 'ALLOCATED', 'CANCELLED') NOT NULL DEFAULT 'WAITING',
    notes VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    emergency_phone VARCHAR(20) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    admin_remarks VARCHAR(255) DEFAULT NULL,
    reviewed_at TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_leave_student (student_id),
    INDEX idx_leave_status (status)
) ENGINE=InnoDB;

-- 8. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    category ENUM('PLUMBING', 'ELECTRICITY', 'CLEANING', 'ROOM', 'INTERNET', 'FURNITURE', 'FOOD', 'OTHER') NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    affected_students_count INT NOT NULL DEFAULT 1,
    severity_score INT NOT NULL DEFAULT 1,
    calculated_priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'LOW',
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    resolution_notes TEXT DEFAULT NULL,
    resolved_at TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_complaints_student (student_id),
    INDEX idx_complaints_priority (calculated_priority),
    INDEX idx_complaints_status (status)
) ENGINE=InnoDB;

-- 9. Medical Requests Table
CREATE TABLE IF NOT EXISTS medical_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    request_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    urgency ENUM('LOW', 'MEDIUM', 'HIGH', 'EMERGENCY') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    doctor_notes TEXT DEFAULT NULL,
    doctor_assigned VARCHAR(100) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_medical_student (student_id)
) ENGINE=InnoDB;

-- 10. Fees Table
CREATE TABLE IF NOT EXISTS fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fee_code VARCHAR(30) NOT NULL UNIQUE,
    student_id INT NOT NULL,
    fee_type ENUM('HOSTEL_RENT', 'MESS_FEE', 'MAINTENANCE', 'SECURITY_DEPOSIT', 'OTHER') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('UNPAID', 'PARTIALLY_PAID', 'PAID', 'OVERDUE') NOT NULL DEFAULT 'UNPAID',
    academic_term VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_fees_student (student_id),
    INDEX idx_fees_status (status)
) ENGINE=InnoDB;

-- 11. Fee Payments Table
CREATE TABLE IF NOT EXISTS fee_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fee_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method ENUM('CASH', 'ONLINE', 'BANK_TRANSFER', 'CHEQUE') NOT NULL DEFAULT 'ONLINE',
    transaction_reference VARCHAR(100) NOT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE CASCADE,
    INDEX idx_payments_fee (fee_id)
) ENGINE=InnoDB;

-- 12. Notices Table
CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    category ENUM('GENERAL', 'MESS', 'ACADEMIC', 'EMERGENCY', 'MAINTENANCE') NOT NULL DEFAULT 'GENERAL',
    created_by INT NOT NULL,
    created_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admins(id) ON DELETE CASCADE,
    INDEX idx_notices_active (is_active, expiry_date)
) ENGINE=InnoDB;

-- 13. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,
    type ENUM('LEAVE', 'COMPLAINT', 'LOST_FOUND', 'NOTICE', 'MESS', 'SYSTEM', 'MEDICAL', 'FEE') NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id, is_read)
) ENGINE=InnoDB;

-- 14. Lost Items Table
CREATE TABLE IF NOT EXISTS lost_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    category ENUM('ELECTRONICS', 'CLOTHING', 'DOCUMENTS', 'ACCESSORIES', 'KEYS', 'BOOKS', 'OTHER') NOT NULL,
    description TEXT NOT NULL,
    color VARCHAR(30) DEFAULT NULL,
    brand VARCHAR(50) DEFAULT NULL,
    last_known_location VARCHAR(100) NOT NULL,
    lost_date DATE NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    status ENUM('OPEN', 'POSSIBLE_MATCH', 'CLAIMED', 'RETURNED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_lost_status (status)
) ENGINE=InnoDB;

-- 15. Found Items Table
CREATE TABLE IF NOT EXISTS found_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    category ENUM('ELECTRONICS', 'CLOTHING', 'DOCUMENTS', 'ACCESSORIES', 'KEYS', 'BOOKS', 'OTHER') NOT NULL,
    description TEXT NOT NULL,
    color VARCHAR(30) DEFAULT NULL,
    brand VARCHAR(50) DEFAULT NULL,
    found_location VARCHAR(100) NOT NULL,
    found_date DATE NOT NULL,
    storage_location VARCHAR(100) NOT NULL DEFAULT 'Warden Office',
    status ENUM('OPEN', 'POSSIBLE_MATCH', 'VERIFIED', 'CLAIMED', 'RETURNED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_found_status (status)
) ENGINE=InnoDB;

-- 16. Lost & Found Matches Table
CREATE TABLE IF NOT EXISTS lost_found_matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id INT NOT NULL,
    found_item_id INT NOT NULL,
    similarity_score DECIMAL(5,2) NOT NULL,
    matched_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('POSSIBLE', 'VERIFIED', 'REJECTED', 'RESOLVED') NOT NULL DEFAULT 'POSSIBLE',
    verified_by INT DEFAULT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lost_item_id) REFERENCES lost_items(id) ON DELETE CASCADE,
    FOREIGN KEY (found_item_id) REFERENCES found_items(id) ON DELETE CASCADE,
    FOREIGN KEY (verified_by) REFERENCES admins(id) ON DELETE SET NULL,
    CONSTRAINT unq_match UNIQUE (lost_item_id, found_item_id)
) ENGINE=InnoDB;

-- 17. Meal Attendance Table
CREATE TABLE IF NOT EXISTS meal_attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    meal_date DATE NOT NULL,
    meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER') NOT NULL,
    attendance ENUM('YES', 'NO') NOT NULL,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT unq_student_meal UNIQUE (student_id, meal_date, meal_type),
    INDEX idx_meal_attendance_date (meal_date, meal_type)
) ENGINE=InnoDB;

-- 18. Meal Predictions Table
CREATE TABLE IF NOT EXISTS meal_predictions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prediction_date DATE NOT NULL,
    meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER') NOT NULL,
    total_occupancy INT NOT NULL,
    predicted_attendance INT NOT NULL,
    algorithm_method VARCHAR(50) NOT NULL DEFAULT 'Weighted Historical Moving Average',
    confidence_score DECIMAL(5,2) NOT NULL DEFAULT 85.00,
    notes VARCHAR(255) DEFAULT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unq_prediction UNIQUE (prediction_date, meal_type)
) ENGINE=InnoDB;

-- 19. Food Waste Records Table
CREATE TABLE IF NOT EXISTS food_waste_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_date DATE NOT NULL,
    meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER') NOT NULL,
    food_prepared_headcount INT NOT NULL,
    expected_attendance INT NOT NULL,
    actual_attendance INT NOT NULL,
    remaining_food_headcount INT NOT NULL,
    estimated_waste_kg DECIMAL(6,2) NOT NULL,
    waste_percentage DECIMAL(5,2) NOT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    recorded_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recorded_by) REFERENCES admins(id) ON DELETE CASCADE,
    CONSTRAINT unq_waste_record UNIQUE (record_date, meal_type)
) ENGINE=InnoDB;
