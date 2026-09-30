# Database Design — Smart Hostel Management System

Database: `hostel_management_db` (MySQL 8.0+)

## Relational Tables (19 Normalized Entities)

1. **`users`**: Authentication credentials, role (`ADMIN`, `STUDENT`), salt, password hash.
2. **`admins`**: Administrator profile information.
3. **`students`**: Student profiles, emergency contacts, code (`STU1001`).
4. **`rooms`**: Room numbers, block, floor, capacity, current occupancy, rent.
5. **`room_allocations`**: Tracks current and historic student room assignments.
6. **`waiting_list`**: Student room preference queue.
7. **`leave_requests`**: Start/end dates, reason, status (`PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`).
8. **`complaints`**: Issues categorized with automated priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
9. **`medical_requests`**: Urgency, assigned doctor, status.
10. **`fees`**: Total fee amount, due dates, statuses (`UNPAID`, `PARTIALLY_PAID`, `PAID`).
11. **`fee_payments`**: Individual payment transaction records.
12. **`notices`**: Public notices with expiration dates.
13. **`notifications`**: User alert system.
14. **`lost_items`**: Reports of misplaced student belongings.
15. **`found_items`**: Reports of discovered items stored in Warden office.
16. **`lost_found_matches`**: Calculated similarity scores (0-100%) connecting lost & found items.
17. **`meal_attendance`**: Daily meal check-ins (`BREAKFAST`, `LUNCH`, `DINNER`).
18. **`meal_predictions`**: Historical statistical forecasts.
19. **`food_waste_records`**: Daily prepared vs consumed metrics with automated waste percentages.
