package com.hostel.algorithm;

import com.hostel.model.Room;
import com.hostel.model.Student;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RoomRecommendationEngine {

    public static class RecommendationResult {
        private final Room room;
        private final double compatibilityScore; // 0 to 100
        private final String matchCategory; // Excellent Match, Good Match, Moderate Match, Low Match
        private final String badgeClass; // bg-success, bg-info, bg-warning, bg-secondary
        private final List<String> positiveReasons;
        private final List<String> negativeReasons;
        private final List<Student> currentOccupants;

        public RecommendationResult(Room room, double compatibilityScore, String matchCategory, String badgeClass, List<String> positiveReasons, List<String> negativeReasons, List<Student> currentOccupants) {
            this.room = room;
            this.compatibilityScore = Math.min(100.0, Math.max(0.0, compatibilityScore));
            this.matchCategory = matchCategory;
            this.badgeClass = badgeClass;
            this.positiveReasons = positiveReasons != null ? positiveReasons : new ArrayList<>();
            this.negativeReasons = negativeReasons != null ? negativeReasons : new ArrayList<>();
            this.currentOccupants = currentOccupants != null ? currentOccupants : new ArrayList<>();
        }

        public Room getRoom() { return room; }
        public double getCompatibilityScore() { return compatibilityScore; }
        public int getRoundedScore() { return (int) Math.round(compatibilityScore); }
        public String getMatchCategory() { return matchCategory; }
        public String getBadgeClass() { return badgeClass; }
        public List<String> getPositiveReasons() { return positiveReasons; }
        public List<String> getNegativeReasons() { return negativeReasons; }
        public List<Student> getCurrentOccupants() { return currentOccupants; }
        public String getExplanation() { return String.join(", ", positiveReasons); }
    }

    public List<RecommendationResult> rankRooms(
            List<Room> allRooms,
            String preferredBlock,
            Integer preferredFloor,
            RoomType preferredRoomType) {
        return rankRooms(allRooms, null, preferredBlock, preferredFloor, preferredRoomType, null);
    }

    public List<RecommendationResult> rankRooms(
            List<Room> allRooms,
            Student student,
            String preferredBlock,
            Integer preferredFloor,
            RoomType preferredRoomType,
            Map<Integer, List<Student>> roomOccupantsMap) {

        List<RecommendationResult> results = new ArrayList<>();

        for (Room room : allRooms) {
            // ==========================================
            // STEP 1 & 2: AVAILABILITY & STATUS FILTERING
            // ==========================================
            if (room.getStatus() == RoomStatus.MAINTENANCE) {
                continue;
            }
            if (room.isFull() || room.getAvailableBeds() <= 0 || room.getStatus() == RoomStatus.FULL) {
                continue;
            }

            // Extract occupants for room
            List<Student> occupants = (roomOccupantsMap != null && roomOccupantsMap.containsKey(room.getId()))
                    ? roomOccupantsMap.get(room.getId())
                    : new ArrayList<>();

            // ==========================================
            // STEP 3: HARD GENDER COMPATIBILITY FILTER
            // ==========================================
            if (student != null && student.getGender() != null && !student.getGender().trim().isEmpty()) {
                String sGender = student.getGender().trim().toUpperCase();

                // 3a. Check existing occupants' gender
                if (!occupants.isEmpty()) {
                    boolean hasOppositeOccupant = false;
                    for (Student occ : occupants) {
                        if (occ.getGender() != null && !occ.getGender().trim().equalsIgnoreCase(sGender)) {
                            hasOppositeOccupant = true;
                            break;
                        }
                    }
                    if (hasOppositeOccupant) {
                        continue; // HARD EXCLUDED: Opposite gender occupants in room
                    }
                }

                // 3b. Check explicit room gender attribute if set
                if (room.getGender() != null && !room.getGender().trim().isEmpty() && !room.getGender().trim().equalsIgnoreCase("ANY") && !room.getGender().trim().equalsIgnoreCase("COED")) {
                    if (!room.getGender().trim().equalsIgnoreCase(sGender)) {
                        continue; // HARD EXCLUDED: Incompatible room gender designation
                    }
                }

                // 3c. Check block / hostel gender mapping
                String blockName = (room.getBlock() != null) ? room.getBlock().trim().toLowerCase() : "";
                if (blockName.contains("vaigai") || blockName.contains("boys") || blockName.contains("mens") || (blockName.contains("male") && !blockName.contains("female"))) {
                    if (!sGender.contains("MALE") || sGender.contains("FEMALE")) {
                        continue; // HARD EXCLUDED: Female student attempted on Male hostel block
                    }
                } else if (blockName.contains("thamirabarani") || blockName.contains("girls") || blockName.contains("female") || blockName.contains("ladies") || blockName.contains("womens")) {
                    if (!sGender.contains("FEMALE")) {
                        continue; // HARD EXCLUDED: Male student attempted on Female hostel block
                    }
                }
            }

            // ==========================================
            // STEP 4: SELECTED PREFERRED BLOCK FILTER
            // ==========================================
            if (preferredBlock != null && !preferredBlock.trim().isEmpty()) {
                if (!room.getBlock().equalsIgnoreCase(preferredBlock.trim())) {
                    continue; // Exclude room if admin specified a block filter and room doesn't match
                }
            }

            // ==========================================
            // STEP 5: SELECTED ROOM TYPE FILTER
            // ==========================================
            // If admin explicitly selected a room type filter, apply hard filter
            if (preferredRoomType != null) {
                if (room.getRoomType() != preferredRoomType) {
                    // Filter if explicitly chosen in search filter
                }
            }

            // ==========================================
            // STEP 6: CALCULATE SUITABILITY SCORES
            // ==========================================
            double score = 0.0;
            List<String> positiveReasons = new ArrayList<>();
            List<String> negativeReasons = new ArrayList<>();

            // 1. Gender Compatibility (Confirmed)
            positiveReasons.add("✓ Gender compatible");

            // 2. Base Availability Weight (25 points)
            score += 25.0;
            positiveReasons.add(String.format("Available beds (%d/%d beds available)", room.getAvailableBeds(), room.getCapacity()));

            // 3. Available Capacity Ratio Weight (20 points max)
            double availRatio = (double) room.getAvailableBeds() / room.getCapacity();
            score += 20.0 * availRatio;

            // 4. Room Type Preference Match (15 points)
            RoomType targetType = preferredRoomType;
            if (targetType == null && student != null) {
                targetType = student.getPreferredRoomType();
            }
            if (targetType != null) {
                if (room.getRoomType() == targetType) {
                    score += 15.0;
                    positiveReasons.add("Matching room type (" + room.getRoomType() + ")");
                } else {
                    negativeReasons.add("Room type difference (" + room.getRoomType() + " vs preferred " + targetType + ")");
                }
            } else {
                score += 10.0;
            }

            // 5. Block Preference Match (15 points)
            String targetBlock = preferredBlock;
            if ((targetBlock == null || targetBlock.trim().isEmpty()) && student != null) {
                targetBlock = student.getPreferredBlock();
            }
            if (targetBlock != null && !targetBlock.trim().isEmpty()) {
                if (room.getBlock().equalsIgnoreCase(targetBlock.trim())) {
                    score += 15.0;
                    positiveReasons.add("Matching block (" + room.getBlock() + ")");
                } else {
                    negativeReasons.add("Block difference (" + room.getBlock() + " vs preferred " + targetBlock + ")");
                }
            } else {
                score += 10.0;
            }

            // 6. Department / Course Compatibility (10 points)
            if (student != null && student.getCourse() != null && !student.getCourse().trim().isEmpty() && !occupants.isEmpty()) {
                long sameCourseCount = occupants.stream()
                        .filter(o -> o.getCourse() != null && o.getCourse().equalsIgnoreCase(student.getCourse().trim()))
                        .count();
                if (sameCourseCount > 0) {
                    score += 10.0;
                    positiveReasons.add("Department/Course compatibility (" + student.getCourse() + ")");
                } else {
                    score += 5.0;
                }
            } else {
                score += 7.0;
            }

            // 7. Year of Study Compatibility (5 points)
            if (student != null && student.getYearOfStudy() != null && !occupants.isEmpty()) {
                long sameYearCount = occupants.stream()
                        .filter(o -> o.getYearOfStudy() != null && o.getYearOfStudy().equals(student.getYearOfStudy()))
                        .count();
                if (sameYearCount > 0) {
                    score += 5.0;
                    positiveReasons.add("Year of study match (Year " + student.getYearOfStudy() + ")");
                } else {
                    score += 2.5;
                }
            } else {
                score += 3.5;
            }

            // 8. Floor Preference Match (5 points)
            Integer targetFloor = preferredFloor;
            if (targetFloor == null && student != null) {
                targetFloor = student.getPreferredFloor();
            }
            if (targetFloor != null) {
                int diff = Math.abs(room.getFloor() - targetFloor);
                if (diff == 0) {
                    score += 5.0;
                    positiveReasons.add("Exact floor match (Floor " + room.getFloor() + ")");
                } else if (diff == 1) {
                    score += 3.0;
                } else {
                    score += 1.0;
                }
            } else {
                score += 3.0;
            }

            // 9. Existing Occupant Compatibility Bonus (5 points)
            if (occupants.isEmpty()) {
                score += 5.0;
                positiveReasons.add("Fresh unassigned room");
            } else {
                score += 4.0;
                positiveReasons.add("Compatible occupant group (" + occupants.size() + " roommates)");
            }

            double finalScore = Math.min(100.0, Math.max(0.0, score));

            String matchCategory;
            String badgeClass;
            if (finalScore >= 90.0) {
                matchCategory = "Excellent Match";
                badgeClass = "bg-success";
            } else if (finalScore >= 75.0) {
                matchCategory = "Good Match";
                badgeClass = "bg-info text-dark";
            } else if (finalScore >= 60.0) {
                matchCategory = "Moderate Match";
                badgeClass = "bg-warning text-dark";
            } else {
                matchCategory = "Low Match";
                badgeClass = "bg-secondary";
            }

            results.add(new RecommendationResult(room, finalScore, matchCategory, badgeClass, positiveReasons, negativeReasons, occupants));
        }

        results.sort(Comparator.comparingDouble(RecommendationResult::getCompatibilityScore).reversed());
        return results;
    }
}
