package com.hostel.algorithm;

import com.hostel.model.FoundItem;
import com.hostel.model.LostItem;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LostFoundMatchingEngine {

    public double calculateSimilarityScore(LostItem lost, FoundItem found) {
        if (lost == null || found == null) {
            return 0.0;
        }

        double totalScore = 0.0;

        // 1. Item Name Similarity (20%)
        double nameScore = computeTextSimilarity(lost.getItemName(), found.getItemName());
        totalScore += nameScore * 20.0;

        // 2. Category Match (20%)
        double categoryScore = (lost.getCategory() == found.getCategory()) ? 1.0 : 0.0;
        totalScore += categoryScore * 20.0;

        // 3. Description Similarity (20%)
        double descScore = computeTextSimilarity(lost.getDescription(), found.getDescription());
        totalScore += descScore * 20.0;

        // 4. Color Match (15%)
        double colorScore = computeTextSimilarity(lost.getColor(), found.getColor());
        totalScore += colorScore * 15.0;

        // 5. Brand Match (10%)
        double brandScore = computeTextSimilarity(lost.getBrand(), found.getBrand());
        totalScore += brandScore * 10.0;

        // 6. Location Proximity (10%)
        double locationScore = computeTextSimilarity(lost.getLastKnownLocation(), found.getFoundLocation());
        totalScore += locationScore * 10.0;

        // 7. Date Proximity (5%)
        double dateScore = 0.0;
        if (lost.getLostDate() != null && found.getFoundDate() != null) {
            long daysDiff = Math.abs(ChronoUnit.DAYS.between(lost.getLostDate(), found.getFoundDate()));
            if (daysDiff == 0) dateScore = 1.0;
            else if (daysDiff <= 1) dateScore = 0.8;
            else if (daysDiff <= 3) dateScore = 0.5;
            else if (daysDiff <= 7) dateScore = 0.2;
            else dateScore = 0.0;
        }
        totalScore += dateScore * 5.0;

        return Math.min(100.0, Math.max(0.0, totalScore));
    }

    private double computeTextSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) return 0.0;
        String s1 = str1.trim().toLowerCase();
        String s2 = str2.trim().toLowerCase();
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;
        if (s1.equals(s2)) return 1.0;
        if (s1.contains(s2) || s2.contains(s1)) return 0.85;

        // Tokenized Jaccard Similarity + Substring match
        Set<String> words1 = new HashSet<>(Arrays.asList(s1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(s2.split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) return 0.0;

        double jaccard = (double) intersection.size() / union.size();
        return jaccard;
    }
}
