# Smart Hostel Management System

A web-based hostel management application built using **Java 21, Spring Boot, Spring Security, MySQL, and Thymeleaf**. The system streamlines hostel administration through student management, room allocation, leave applications, complaint tracking, fee management, and intelligent hostel services.

## Features

### Admin Module
- Manage student details and hostel rooms.
- Allocate rooms with capacity validation.
- Manage room-upgrade waiting lists.
- Review, approve, or reject leave applications.
- Monitor complaints and their priority levels.
- Manage medical requests, fees, and payments.
- Publish notices and send notifications.
- Review Lost & Found matches and manage returned items.
- Monitor meal attendance and predict meal demand.
- Track food preparation, consumption, and waste.
- View reports on occupancy, complaints, leave requests, and outstanding fees.

### Student Module
- View and update profile information.
- View room assignments and roommates.
- Apply for leave and track application status.
- Submit complaints and medical requests.
- View fee details, payments, and outstanding balances.
- Access notices and notifications.
- Report lost or found items and view potential matches.
- Record breakfast, lunch, and dinner attendance.

## Smart Features

### 1. Smart Lost & Found Matching
Calculates a weighted similarity score between lost and found item reports using seven attributes.

| Attribute | Weight |
|---|---:|
| Item Name | 20% |
| Category | 20% |
| Description | 20% |
| Color | 15% |
| Brand | 10% |
| Location | 10% |
| Date Proximity | 5% |

### 2. Smart Meal Demand Prediction
Uses a weighted moving average of historical attendance, adjusted for current hostel occupancy, to estimate meal demand.

```text
Predicted Demand =
    (0.50 × Day 1 Attendance)
  + (0.30 × Day 2 Attendance)
  + (0.20 × Day 3 Attendance)
```

### 3. Food Waste Management
Calculates the estimated percentage of prepared meals that remain unused.

```text
Waste Percentage =
    ((Prepared Headcount - Actual Attendance)
    / Prepared Headcount) × 100
```

### 4. Smart Room Recommendations
Recommends suitable rooms based on room-scoring logic, gender compatibility, and capacity validation.

### 5. Complaint Prioritization
Automatically categorizes complaints into `CRITICAL`, `HIGH`, `MEDIUM`, and `LOW` priority levels.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend development |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| HTML, CSS, JavaScript | Frontend development |
| Thymeleaf | Server-side HTML rendering |
| MySQL | Relational database |
| Spring Data JPA / Hibernate | Database persistence and ORM |
| Maven | Build and dependency management |
| JUnit 5 & Mockito | Automated testing |

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL Server 8+

## Installation and Setup

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_PROJECT_DIRECTORY>
```

### 2. Configure the Database

Create or configure the MySQL database according to your project schema.

Update the database connection settings in `src/main/resources/application.properties` or your project's actual configuration file.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hostel_management_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Configure the environment variables or use the configuration mechanism supported by your application. Never commit real database credentials to GitHub.

### 3. Build the Application

```bash
mvn clean package
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Alternatively, run the executable JAR generated in the `target` directory:

```bash
java -jar target/<generated-jar-name>.jar
```

Open the local URL and port configured by your Spring Boot application in your browser.

## Testing

Run the automated test suite using:

```bash
mvn clean test
```

## Developer

**Nirmal Kumar**

B.Tech Computer Science and Business Systems (CSBS)  
Chennai Institute of Technology

- GitHub: [Nirmalkumar-2008](https://github.com/Nirmalkumar-2008)
- LinkedIn: [Nirmal Kumar](https://www.linkedin.com/in/nirmal-kumar-er)
