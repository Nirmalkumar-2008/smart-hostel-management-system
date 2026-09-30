package com.hostel.algorithm;

import com.hostel.model.enums.ComplaintCategory;
import com.hostel.model.enums.ComplaintPriority;
import org.springframework.stereotype.Service;

@Service
public class ComplaintPriorityService {

    public ComplaintPriority calculatePriority(ComplaintCategory category, int severityScore, int affectedCount, long waitingHours) {
        return ComplaintPriorityEngine.calculatePriority(category, severityScore, affectedCount, waitingHours);
    }
}
