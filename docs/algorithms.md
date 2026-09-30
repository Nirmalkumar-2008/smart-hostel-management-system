# Algorithmic & Prediction Engines — Smart Hostel Management System

## 1. Smart Lost & Found Matching Engine (`LostFoundMatchingEngine.java`)

### Overview
Compares newly reported lost items against all open found items in the database using a multi-attribute weighted similarity scoring model normalized from **0.0% to 100.0%**.

### Attribute Weights
- **Item Name**: 20%
- **Category**: 20%
- **Description**: 20%
- **Color**: 15%
- **Brand**: 10%
- **Location Proximity**: 10%
- **Date Proximity**: 5%

### Formula
$$\text{Similarity Score} = \sum_{i=1}^7 (\text{Attribute Score}_i \times \text{Weight}_i)$$

Matches $\ge 50.0\%$ are recorded in the database and trigger automatic notifications to the student.

---

## 2. Smart Mess Meal Demand Prediction Engine (`MealPredictionEngine.java`)

### Overview
Applies a **Weighted Moving Average** over historical database attendance records for the target meal type and specific day of the week.

### Formula
$$\text{Raw Demand} = \frac{\sum w_i \times \text{Count}_i}{\sum w_i}$$

Weights: $w_1 = 0.50$, $w_2 = 0.30$, $w_3 = 0.20$.

Adjusted by current overall hostel occupancy ratio:
$$\text{Occupancy Factor} = \frac{\text{Current Occupancy}}{\text{Total Capacity}}$$

If historical records $< 1$, the system returns *"Insufficient historical data for reliable prediction."*

---

## 3. Smart Room Recommendation Engine (`RoomRecommendationEngine.java`)

Ranks available rooms based on compatibility points:
- Available capacity ratio: up to 20 pts
- Block match: 35 pts
- Room Type match: 30 pts
- Floor preference match: 15 pts

---

## 4. Smart Complaint Priority Engine (`ComplaintPriorityEngine.java`)

Calculates weighted complaint score:
$$\text{Score} = (\text{CategoryWeight} \times 3.5) + (\text{Severity} \times 4.0) + (\text{AffectedCount} \times 1.5) + (\text{WaitHours} \times 0.2)$$

- $\text{Score} \ge 70$: **CRITICAL**
- $\text{Score} \ge 50$: **HIGH**
- $\text{Score} \ge 30$: **MEDIUM**
- $\text{Score} < 30$: **LOW**
