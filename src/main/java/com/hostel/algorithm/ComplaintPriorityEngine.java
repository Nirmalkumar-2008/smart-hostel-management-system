package com.hostel.algorithm;

import com.hostel.model.enums.ComplaintCategory;
import com.hostel.model.enums.ComplaintPriority;

public class ComplaintPriorityEngine {

    public static ComplaintPriority calculatePriority(ComplaintCategory category, int severityScore, int affectedCount, long waitingHours) {
        double categoryWeight;
        switch (category) {
            case PLUMBING:
            case ELECTRICITY:
                categoryWeight = 9.0;
                break;
            case FOOD:
                categoryWeight = 8.0;
                break;
            case CLEANING:
            case ROOM:
                categoryWeight = 6.0;
                break;
            case INTERNET:
                categoryWeight = 5.0;
                break;
            case FURNITURE:
                categoryWeight = 4.0;
                break;
            case OTHER:
            default:
                categoryWeight = 4.0;
                break;
        }

        // Weighted Formula
        double score = (categoryWeight * 3.5) +
                       (severityScore * 4.0) +
                       (Math.min(affectedCount, 25) * 1.5) +
                       (waitingHours * 0.2);

        if (score >= 70.0) {
            return ComplaintPriority.CRITICAL;
        } else if (score >= 50.0) {
            return ComplaintPriority.HIGH;
        } else if (score >= 30.0) {
            return ComplaintPriority.MEDIUM;
        } else {
            return ComplaintPriority.LOW;
        }
    }
}
