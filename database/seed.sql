-- Smart Hostel Management System Seed Data
-- MySQL 8.0+

USE hostel_management_db;

-- Clear existing data if needed (respecting foreign key checks)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE food_waste_records;
TRUNCATE TABLE meal_predictions;
TRUNCATE TABLE meal_attendance;
TRUNCATE TABLE lost_found_matches;
TRUNCATE TABLE found_items;
TRUNCATE TABLE lost_items;
TRUNCATE TABLE notifications;
TRUNCATE TABLE notices;
TRUNCATE TABLE fee_payments;
TRUNCATE TABLE fees;
TRUNCATE TABLE medical_requests;
TRUNCATE TABLE complaints;
TRUNCATE TABLE leave_requests;
TRUNCATE TABLE waiting_list;
TRUNCATE TABLE room_allocations;
TRUNCATE TABLE rooms;
TRUNCATE TABLE students;
TRUNCATE TABLE admins;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users
-- SHA-256 of ('admin123' + 'salt_admin_123456') = 1939f07a42f994e5debafbf822a42d2cfe3e84b3446afbcfaf214a3fdd0eb5f7
-- SHA-256 of ('student123' + 'salt_student_1234') = 37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44

INSERT INTO users (id, username, password_hash, password_salt, role, status) VALUES
(1, 'admin', '1939f07a42f994e5debafbf822a42d2cfe3e84b3446afbcfaf214a3fdd0eb5f7', 'salt_admin_123456', 'ADMIN', 'ACTIVE'),
(2, 'alex_rover', '37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44', 'salt_student_1234', 'STUDENT', 'ACTIVE'),
(3, 'emma_watson', '37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44', 'salt_student_1234', 'STUDENT', 'ACTIVE'),
(4, 'david_miller', '37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44', 'salt_student_1234', 'STUDENT', 'ACTIVE'),
(5, 'sophia_chen', '37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44', 'salt_student_1234', 'STUDENT', 'ACTIVE'),
(6, 'rahul_sharma', '37768b80ea6e01570f09201ba127a6060b51f59255d9b4fee140926d8b2e7f44', 'salt_student_1234', 'STUDENT', 'ACTIVE');

-- 2. Admins
INSERT INTO admins (id, user_id, full_name, email, phone, department) VALUES
(1, 1, 'Chief Warden Administrator', 'admin@hostel.edu', '+1-555-0199', 'Hostel Operations');

-- 3. Students
INSERT INTO students (id, user_id, student_code, full_name, email, phone, gender, course, year_of_study, emergency_contact, status) VALUES
(1, 2, 'STU1001', 'Alex Rover', 'alex.rover@student.edu', '+1-555-0101', 'MALE', 'Computer Science', 3, '+1-555-9901', 'ACTIVE'),
(2, 3, 'STU1002', 'Emma Watson', 'emma.watson@student.edu', '+1-555-0102', 'FEMALE', 'Information Technology', 2, '+1-555-9902', 'ACTIVE'),
(3, 4, 'STU1003', 'David Miller', 'david.miller@student.edu', '+1-555-0103', 'MALE', 'Mechanical Engineering', 4, '+1-555-9903', 'ACTIVE'),
(4, 5, 'STU1004', 'Sophia Chen', 'sophia.chen@student.edu', '+1-555-0104', 'FEMALE', 'Electrical Engineering', 1, '+1-555-9904', 'ACTIVE'),
(5, 6, 'STU1005', 'Rahul Sharma', 'rahul.sharma@student.edu', '+1-555-0105', 'MALE', 'Civil Engineering', 2, '+1-555-9905', 'ACTIVE');

-- 4. Rooms
INSERT INTO rooms (id, room_number, block, floor, capacity, current_occupancy, room_type, status, base_monthly_rent) VALUES
(1, 'A101', 'A', 1, 2, 2, 'DOUBLE', 'FULL', 6000.00),
(2, 'A102', 'A', 1, 2, 1, 'DOUBLE', 'AVAILABLE', 6000.00),
(3, 'B201', 'B', 2, 1, 1, 'SINGLE', 'FULL', 8500.00),
(4, 'B202', 'B', 2, 1, 0, 'SINGLE', 'AVAILABLE', 8500.00),
(5, 'B204', 'B', 2, 2, 1, 'DOUBLE', 'AVAILABLE', 6500.00),
(6, 'C301', 'C', 3, 3, 0, 'TRIPLE', 'AVAILABLE', 4500.00),
(7, 'C302', 'C', 3, 4, 0, 'DORMITORY', 'MAINTENANCE', 3500.00);

-- 5. Room Allocations
INSERT INTO room_allocations (id, student_id, room_id, allocated_date, status, notes) VALUES
(1, 1, 1, '2026-01-10', 'ACTIVE', 'Regular room allocation'),
(2, 2, 1, '2026-01-10', 'ACTIVE', 'Regular room allocation'),
(3, 3, 3, '2026-01-12', 'ACTIVE', 'Single room allocation'),
(4, 4, 2, '2026-01-15', 'ACTIVE', 'Double room allocation'),
(5, 5, 5, '2026-01-20', 'ACTIVE', 'Double room allocation');

-- 6. Waiting List
INSERT INTO waiting_list (id, student_id, preferred_block, preferred_room_type, request_date, priority_score, status, notes) VALUES
(1, 5, 'B', 'SINGLE', '2026-02-01', 85, 'WAITING', 'Requested upgrade to single room');

-- 7. Leave Requests
INSERT INTO leave_requests (id, student_id, start_date, end_date, reason, destination, emergency_phone, status, admin_remarks, reviewed_at) VALUES
(1, 1, '2026-08-15', '2026-08-18', 'Family function at home town', 'Chicago, IL', '+1-555-9901', 'APPROVED', 'Approved by Warden', '2026-08-10 10:00:00'),
(2, 2, '2026-08-20', '2026-08-22', 'Medical checkup and consultation', 'Boston, MA', '+1-555-9902', 'PENDING', NULL, NULL);

-- 8. Complaints
INSERT INTO complaints (id, student_id, category, title, description, location, affected_students_count, severity_score, calculated_priority, status) VALUES
(1, 1, 'PLUMBING', 'Water Leakage in Bathroom', 'Major pipe leak flooding bathroom floor on 1st floor Block A', 'Block A - Room 101 Bathroom', 8, 9, 'CRITICAL', 'OPEN'),
(2, 3, 'INTERNET', 'Slow Wi-Fi Connection', 'Wi-Fi disconnects frequently during online lectures', 'Block B - 2nd Floor', 4, 3, 'MEDIUM', 'IN_PROGRESS'),
(3, 4, 'FURNITURE', 'Broken Study Chair', 'Desk chair leg is cracked and unstable', 'Block A - Room 102', 1, 2, 'LOW', 'OPEN');

-- 9. Medical Requests
INSERT INTO medical_requests (id, student_id, request_type, description, urgency, status, doctor_notes, doctor_assigned) VALUES
(1, 2, 'High Fever & Flu', 'Running 102F fever with severe headache and weakness', 'HIGH', 'ACCEPTED', 'Prescribed paracetamol and hydration rest', 'Dr. Robert Vance');

-- 10. Fees
INSERT INTO fees (id, fee_code, student_id, fee_type, total_amount, due_date, status, academic_term) VALUES
(1, 'FEE-2026-001', 1, 'HOSTEL_RENT', 18000.00, '2026-08-31', 'PAID', 'Fall 2026'),
(2, 'FEE-2026-002', 2, 'HOSTEL_RENT', 18000.00, '2026-08-31', 'PARTIALLY_PAID', 'Fall 2026'),
(3, 'FEE-2026-003', 3, 'HOSTEL_RENT', 25500.00, '2026-08-31', 'UNPAID', 'Fall 2026');

-- 11. Fee Payments
INSERT INTO fee_payments (id, fee_id, amount_paid, payment_date, payment_method, transaction_reference, notes) VALUES
(1, 1, 18000.00, '2026-08-01', 'ONLINE', 'TXN994827164', 'Full payment clear'),
(2, 2, 10000.00, '2026-08-05', 'BANK_TRANSFER', 'TXN112048577', 'First installment');

-- 12. Notices
INSERT INTO notices (id, title, content, category, created_by, created_date, expiry_date, is_active) VALUES
(1, 'Hostel Maintenance Schedule', 'Plumbing maintenance will occur on Saturday from 10 AM to 2 PM.', 'MAINTENANCE', 1, '2026-08-05', '2026-08-25', TRUE),
(2, 'Special Sunday Mess Menu', 'Chef special dinner menu scheduled for Sunday evening 7 PM - 9:30 PM.', 'MESS', 1, '2026-08-08', '2026-08-30', TRUE);

-- 13. Notifications
INSERT INTO notifications (id, user_id, title, message, type, is_read) VALUES
(1, 2, 'Leave Request Approved', 'Your leave request for Aug 15 - Aug 18 has been approved.', 'LEAVE', TRUE),
(2, 3, 'Fee Payment Reminder', 'Hostel rent due date is Aug 31, 2026. Remaining: $8,000.00', 'FEE', FALSE);

-- 14. Lost Items
INSERT INTO lost_items (id, student_id, item_name, category, description, color, brand, last_known_location, lost_date, contact_phone, status) VALUES
(1, 1, 'Steel Water Bottle', 'OTHER', 'Blue steel water bottle with black cap and sticker', 'Blue', 'Milton', 'Mess Hall Block A', '2026-08-08', '+1-555-0101', 'OPEN');

-- 15. Found Items
INSERT INTO found_items (id, student_id, item_name, category, description, color, brand, found_location, found_date, storage_location, status) VALUES
(1, 3, 'Steel Water Bottle', 'OTHER', 'Dark blue steel bottle with black lid and sticker', 'Blue', 'Milton', 'Dining Table 4 Mess Hall', '2026-08-08', 'Warden Office Counter', 'OPEN');

-- 16. Lost & Found Matches
INSERT INTO lost_found_matches (id, lost_item_id, found_item_id, similarity_score, status, notes) VALUES
(1, 1, 1, 91.50, 'POSSIBLE', 'High similarity match generated by matching algorithm');

-- 17. Historical Meal Attendance (30 Days of realistic mess records)
INSERT INTO meal_attendance (student_id, meal_date, meal_type, attendance, marked_at) VALUES
-- Day 1
(1, '2026-07-10', 'BREAKFAST', 'YES', '2026-07-10 08:00:00'), (1, '2026-07-10', 'LUNCH', 'YES', '2026-07-10 12:30:00'), (1, '2026-07-10', 'DINNER', 'YES', '2026-07-10 19:30:00'),
(2, '2026-07-10', 'BREAKFAST', 'YES', '2026-07-10 08:15:00'), (2, '2026-07-10', 'LUNCH', 'YES', '2026-07-10 13:00:00'), (2, '2026-07-10', 'DINNER', 'YES', '2026-07-10 20:00:00'),
(3, '2026-07-10', 'BREAKFAST', 'YES', '2026-07-10 08:30:00'), (3, '2026-07-10', 'LUNCH', 'NO', '2026-07-10 13:00:00'), (3, '2026-07-10', 'DINNER', 'YES', '2026-07-10 19:45:00'),
(4, '2026-07-10', 'BREAKFAST', 'NO', '2026-07-10 08:30:00'), (4, '2026-07-10', 'LUNCH', 'YES', '2026-07-10 12:45:00'), (4, '2026-07-10', 'DINNER', 'YES', '2026-07-10 19:15:00'),
(5, '2026-07-10', 'BREAKFAST', 'YES', '2026-07-10 08:00:00'), (5, '2026-07-10', 'LUNCH', 'YES', '2026-07-10 12:30:00'), (5, '2026-07-10', 'DINNER', 'YES', '2026-07-10 19:30:00'),

-- Recent Days (Today & Past 5 Days)
(1, '2026-08-08', 'BREAKFAST', 'YES', '2026-08-08 08:00:00'), (1, '2026-08-08', 'LUNCH', 'YES', '2026-08-08 12:30:00'), (1, '2026-08-08', 'DINNER', 'YES', '2026-08-08 19:30:00'),
(2, '2026-08-08', 'BREAKFAST', 'YES', '2026-08-08 08:15:00'), (2, '2026-08-08', 'LUNCH', 'YES', '2026-08-08 13:00:00'), (2, '2026-08-08', 'DINNER', 'YES', '2026-08-08 20:00:00'),
(3, '2026-08-08', 'BREAKFAST', 'NO', '2026-08-08 08:30:00'), (3, '2026-08-08', 'LUNCH', 'YES', '2026-08-08 13:00:00'), (3, '2026-08-08', 'DINNER', 'YES', '2026-08-08 19:45:00'),
(4, '2026-08-08', 'BREAKFAST', 'YES', '2026-08-08 08:30:00'), (4, '2026-08-08', 'LUNCH', 'YES', '2026-08-08 12:45:00'), (4, '2026-08-08', 'DINNER', 'YES', '2026-08-08 19:15:00'),
(5, '2026-08-08', 'BREAKFAST', 'YES', '2026-08-08 08:00:00'), (5, '2026-08-08', 'LUNCH', 'YES', '2026-08-08 12:30:00'), (5, '2026-08-08', 'DINNER', 'NO', '2026-08-08 19:30:00'),

(1, '2026-08-09', 'BREAKFAST', 'YES', '2026-08-09 08:00:00'), (1, '2026-08-09', 'LUNCH', 'YES', '2026-08-09 12:30:00'), (1, '2026-08-09', 'DINNER', 'YES', '2026-08-09 19:30:00'),
(2, '2026-08-09', 'BREAKFAST', 'YES', '2026-08-09 08:15:00'), (2, '2026-08-09', 'LUNCH', 'YES', '2026-08-09 13:00:00'), (2, '2026-08-09', 'DINNER', 'YES', '2026-08-09 20:00:00'),
(3, '2026-08-09', 'BREAKFAST', 'YES', '2026-08-09 08:30:00'), (3, '2026-08-09', 'LUNCH', 'YES', '2026-08-09 13:00:00'), (3, '2026-08-09', 'DINNER', 'YES', '2026-08-09 19:45:00'),
(4, '2026-08-09', 'BREAKFAST', 'YES', '2026-08-09 08:30:00'), (4, '2026-08-09', 'LUNCH', 'NO', '2026-08-09 12:45:00'), (4, '2026-08-09', 'DINNER', 'YES', '2026-08-09 19:15:00'),
(5, '2026-08-09', 'BREAKFAST', 'YES', '2026-08-09 08:00:00'), (5, '2026-08-09', 'LUNCH', 'YES', '2026-08-09 12:30:00'), (5, '2026-08-09', 'DINNER', 'YES', '2026-08-09 19:30:00');

-- 18. Food Waste Records
INSERT INTO food_waste_records (id, record_date, meal_type, food_prepared_headcount, expected_attendance, actual_attendance, remaining_food_headcount, estimated_waste_kg, waste_percentage, notes, recorded_by) VALUES
(1, '2026-08-08', 'BREAKFAST', 420, 400, 385, 35, 14.50, 8.33, 'Normal breakfast waste', 1),
(2, '2026-08-08', 'LUNCH', 450, 430, 425, 25, 10.20, 5.56, 'Efficient lunch consumption', 1),
(3, '2026-08-08', 'DINNER', 440, 420, 390, 50, 21.00, 11.36, 'High remaining quantity', 1);
