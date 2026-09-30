package com.hostel.algorithm;

import com.hostel.model.Room;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomRecommendationEngineTest {

    @Test
    @DisplayName("Should rank exact block and room type matches highest")
    void testRankRooms() {
        Room r1 = new Room(1, "A101", "A", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);
        Room r2 = new Room(2, "B204", "B", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6500.0);
        Room r3 = new Room(3, "B201", "B", 2, 1, 0, RoomType.SINGLE, RoomStatus.AVAILABLE, 8500.0);

        List<Room> rooms = List.of(r1, r2, r3);
        RoomRecommendationEngine engine = new RoomRecommendationEngine();

        List<RoomRecommendationEngine.RecommendationResult> results = engine.rankRooms(rooms, "B", 2, RoomType.DOUBLE);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("B204", results.get(0).getRoom().getRoomNumber());
        assertTrue(results.get(0).getCompatibilityScore() > results.get(1).getCompatibilityScore());
    }
}
