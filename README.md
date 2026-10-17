# Smart Hostel Management System

A Java 21 console application that simplifies hostel administration through student management, room allocation, complaint handling, fee tracking, smart Lost & Found matching, meal demand prediction, and food waste management.

## Features

### Admin Module
- Manage student and room details.
- Allocate rooms with capacity validation.
- Manage leave applications and waiting lists.
- Track complaints and prioritize them by severity.
- Manage medical requests, fees, and payments.
- Publish notices and send notifications.
- Review Lost & Found matches.
- Monitor mess attendance and meal demand.
- Track food waste and view hostel reports.

### Student Module
- View and update profile information.
- View room assignments and roommates.
- Apply for leave and track approval status.
- Submit complaints and medical requests.
- View fee details and payment balances.
- Access notices and notifications.
- Report lost or found items.
- Record breakfast, lunch, and dinner attendance.

## Smart Algorithms

**1. Lost & Found Matching**

Calculates a weighted similarity score using seven attributes:

| Attribute | Weight |
|---|---:|
| Item Name | 20% |
| Category | 20% |
| Description | 20% |
| Color | 15% |
| Brand | 10% |
| Location | 10% |
| Date Proximity | 5% |

**2. Meal Demand Prediction**

Uses a weighted moving average of historical attendance, adjusted by the current hostel occupancy ratio.

```text
Predicted Demand =
    (0.50 × Day 1 Attendance)
  + (0.30 × Day 2 Attendance)
  + (0.20 × Day 3 Attendance)
```

**3. Food Waste Calculation**

```text
Waste Percentage =
    ((Prepared Headcount - Actual Attendance)
    / Prepared Headcount) × 100
```

**4. Room Recommendation**

Recommends suitable rooms based on the implemented room-scoring logic, gender compatibility, and capacity validation.

**5. Complaint Prioritization**

Assigns calculated priority levels: `CRITICAL`, `HIGH`, `MEDIUM`, and `LOW`.

## Technology Stack

- **Language:** Java 21
- **Build Tool:** Maven 3.9+
- **Database:** MySQL 8+
- **Database Connectivity:** JDBC
- **MySQL Driver:** MySQL Connector/J 8.3.0
- **Testing:** JUnit 5 and Mockito

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL Server 8+

## Installation and Setup

**1. Clone the repository**

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_PROJECT_DIRECTORY>
```

**2. Configure the database**

Update `src/main/resources/config.properties` with your local MySQL connection details. Keep database credentials private and never commit real passwords to GitHub.

**3. Run tests**

```bash
mvn clean test
```

**4. Build the application**

```bash
mvn clean package
```

**5. Run the application**

Use the executable JAR generated in the `target` directory. For example, if your build produces the following file:

```bash
java -jar target/hostel-management-system-1.0.0-jar-with-dependencies.jar
```

Ensure the filename matches your actual Maven build output.

## Testing

Run the automated test suite using JUnit 5 and Mockito:

```bash
mvn clean test
```

## Developer

**Nirmal Kumar**

B.Tech Computer Science and Business Systems (CSBS), Chennai Institute of Technology.

- [GitHub](https://github.com/Nirmalkumar-2008)
- [LinkedIn](https://www.linkedin.com/in/nirmal-kumar-er)

