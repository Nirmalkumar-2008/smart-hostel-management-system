package com.hostel.algorithm;

import com.hostel.model.Room;
import com.hostel.model.Student;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoomRecommendationService {

    private final RoomRepository roomRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final StudentRepository studentRepository;
    private final RoomRecommendationEngine engine = new RoomRecommendationEngine();

    public RoomRecommendationService(RoomRepository roomRepository,
                                     RoomAllocationRepository roomAllocationRepository,
                                     StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.roomAllocationRepository = roomAllocationRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomRecommendationEngine.RecommendationResult> getRankedRecommendations(
            Student student,
            String preferredBlock,
            Integer preferredFloor,
            RoomType preferredRoomType) {

        List<Room> allRooms = roomRepository.findAll();

        // Build occupants map for all rooms
        Map<Integer, List<Student>> roomOccupantsMap = new HashMap<>();
        for (Room room : allRooms) {
            var activeAllocations = roomAllocationRepository.findByRoomIdAndStatus(room.getId(), "ACTIVE");
            List<Student> occupants = new ArrayList<>();
            for (var alloc : activeAllocations) {
                studentRepository.findById(alloc.getStudentId()).ifPresent(occupants::add);
            }
            roomOccupantsMap.put(room.getId(), occupants);
        }

        return engine.rankRooms(allRooms, student, preferredBlock, preferredFloor, preferredRoomType, roomOccupantsMap);
    }

    @Transactional(readOnly = true)
    public List<RoomRecommendationEngine.RecommendationResult> rankRooms(
            List<Room> availableRooms,
            String preferredBlock,
            Integer preferredFloor,
            RoomType preferredRoomType) {
        return getRankedRecommendations(null, preferredBlock, preferredFloor, preferredRoomType);
    }
}
