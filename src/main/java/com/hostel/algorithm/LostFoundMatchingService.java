package com.hostel.algorithm;

import com.hostel.model.FoundItem;
import com.hostel.model.LostItem;
import org.springframework.stereotype.Service;

@Service
public class LostFoundMatchingService {

    private final LostFoundMatchingEngine engine = new LostFoundMatchingEngine();

    public double calculateSimilarityScore(LostItem lost, FoundItem found) {
        return engine.calculateSimilarityScore(lost, found);
    }
}
