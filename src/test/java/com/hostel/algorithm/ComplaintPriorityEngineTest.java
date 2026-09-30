package com.hostel.algorithm;

import com.hostel.model.enums.ComplaintCategory;
import com.hostel.model.enums.ComplaintPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplaintPriorityEngineTest {

    @Test
    @DisplayName("Should assign CRITICAL priority to severe plumbing issue affecting multiple students")
    void testCriticalPriority() {
        ComplaintPriority priority = ComplaintPriorityEngine.calculatePriority(ComplaintCategory.PLUMBING, 9, 15, 2);
        assertEquals(ComplaintPriority.CRITICAL, priority);
    }

    @Test
    @DisplayName("Should assign LOW priority to minor furniture issue affecting single student")
    void testLowPriority() {
        ComplaintPriority priority = ComplaintPriorityEngine.calculatePriority(ComplaintCategory.FURNITURE, 2, 1, 0);
        assertEquals(ComplaintPriority.LOW, priority);
    }
}
