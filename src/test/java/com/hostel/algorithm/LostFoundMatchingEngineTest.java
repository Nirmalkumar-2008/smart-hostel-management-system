package com.hostel.algorithm;

import com.hostel.model.FoundItem;
import com.hostel.model.LostItem;
import com.hostel.model.enums.LostFoundCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LostFoundMatchingEngineTest {

    @Test
    @DisplayName("Should generate high similarity score (>85%) for matching lost and found items")
    void testLostFoundSimilarityMatching() {
        LostItem lost = new LostItem();
        lost.setItemName("Blue steel water bottle");
        lost.setCategory(LostFoundCategory.OTHER);
        lost.setDescription("Blue steel water bottle with black cap and sticker");
        lost.setColor("Blue");
        lost.setBrand("Milton");
        lost.setLastKnownLocation("Mess Hall Block A");
        lost.setLostDate(LocalDate.now());

        FoundItem found = new FoundItem();
        found.setItemName("Steel water bottle");
        found.setCategory(LostFoundCategory.OTHER);
        found.setDescription("Dark blue steel bottle with black lid and sticker");
        found.setColor("Blue");
        found.setBrand("Milton");
        found.setFoundLocation("Dining Table 4 Mess Hall");
        found.setFoundDate(LocalDate.now());

        LostFoundMatchingEngine engine = new LostFoundMatchingEngine();
        double score = engine.calculateSimilarityScore(lost, found);

        assertTrue(score >= 80.0, "Score should be >= 80%, actual: " + score);
    }
}
