# System Architecture — Smart Hostel Management System

The **Smart Hostel Management System** is built using a clean, layered architectural pattern in **Java 21**, communicating with a **MySQL 8+** database via **JDBC**.

## Layer Diagram

```
+-------------------------------------------------------+
|                    Main Application                   |
|       (com.hostel.main.HostelManagementApplication)    |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|                       Console UI                      |
|           (MainMenu / AdminMenu / StudentMenu)         |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|                   Controller Layer                    |
|           (AdminController / StudentController)       |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|                     Service Layer                     |
| (AuthService, StudentService, RoomService, MessService)|
+-------------------------------------------------------+
         |                                   |
         v                                   v
+-----------------------+         +---------------------+
|  Algorithmic Engines  |         |   Prediction Engine |
| (RoomRec, LostFound,  |         |  (MealPrediction)   |
|  ComplaintPriority)   |         +---------------------+
+-----------------------+                    |
         |                                   |
         +-----------------+-----------------+
                           |
                           v
+-------------------------------------------------------+
|                       DAO Layer                       |
| (UserDAO, RoomDAO, StudentDAO, LeaveDAO, FeeDAO, etc.)|
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|                Database Connection & JDBC             |
|      (DatabaseConnection / Connection Pooling)        |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|                   MySQL 8+ Database                   |
|                (hostel_management_db)                 |
+-------------------------------------------------------+
```

## Security & Reliability Highlights
1. **Password Security**: SHA-256 password hashing with unique 16-byte random salts per user.
2. **SQL Injection Protection**: PreparedStatements used across all DAOs.
3. **Transaction Safety**: ACID transactions for critical multi-table mutations (such as Room Allocation & Deallocation).
4. **Input Resilience**: Custom Scanner utilities (`InputUtil`) intercepting invalid token types and preventing crashes.
