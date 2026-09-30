package com.hostel.algorithm;

import com.hostel.exception.HostelException;
import com.hostel.model.Room;
import com.hostel.model.RoomAllocation;
import com.hostel.model.Student;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.WaitingListRepository;
import com.hostel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmartRoomAllocationTest {

    private RoomRepository roomRepository;
    private RoomAllocationRepository roomAllocationRepository;
    private StudentRepository studentRepository;
    private WaitingListRepository waitingListRepository;
    private RoomService roomService;
    private RoomRecommendationEngine recommendationEngine;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomAllocationRepository = mock(RoomAllocationRepository.class);
        studentRepository = mock(StudentRepository.class);
        waitingListRepository = mock(WaitingListRepository.class);

        roomService = new RoomService(roomRepository, roomAllocationRepository, studentRepository, waitingListRepository);
        recommendationEngine = new RoomRecommendationEngine();
    }

    @Test
    @DisplayName("TEST 1: Male student + male room -> Recommended")
    void testMaleStudentMaleRoomRecommended() {
        Student maleStudent = new Student(1, 10, "STU1001", "Arun Kumar", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room vaigaiRoom = new Room(101, "V-101", "Vaigai", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(vaigaiRoom), maleStudent, null, null, null, Collections.emptyMap()
        );

        assertEquals(1, results.size());
        assertEquals("V-101", results.get(0).getRoom().getRoomNumber());
        assertTrue(results.get(0).getPositiveReasons().contains("✓ Gender compatible"));
    }

    @Test
    @DisplayName("TEST 2: Male student + female room -> Completely excluded (hard filter)")
    void testMaleStudentFemaleRoomCompletelyExcluded() {
        Student maleStudent = new Student(1, 10, "STU1001", "Arun Kumar", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room thamirabaraniRoom = new Room(201, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(thamirabaraniRoom), maleStudent, null, null, null, Collections.emptyMap()
        );

        assertTrue(results.isEmpty(), "Female hostel room must be HARD EXCLUDED for male student before scoring");
    }

    @Test
    @DisplayName("TEST 3: Female student + female room -> Recommended")
    void testFemaleStudentFemaleRoomRecommended() {
        Student femaleStudent = new Student(2, 11, "STU1002", "Priya Sharma", "priya@example.com", "9876543210", "FEMALE", "IT", 3, "9876543210", "ACTIVE");
        Room thamirabaraniRoom = new Room(201, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(thamirabaraniRoom), femaleStudent, null, null, null, Collections.emptyMap()
        );

        assertEquals(1, results.size());
        assertEquals("T-201", results.get(0).getRoom().getRoomNumber());
        assertTrue(results.get(0).getPositiveReasons().contains("✓ Gender compatible"));
    }

    @Test
    @DisplayName("TEST 4: Female student + male room -> Completely excluded (hard filter)")
    void testFemaleStudentMaleRoomCompletelyExcluded() {
        Student femaleStudent = new Student(2, 11, "STU1002", "Priya Sharma", "priya@example.com", "9876543210", "FEMALE", "IT", 3, "9876543210", "ACTIVE");
        Room vaigaiRoom = new Room(101, "V-101", "Vaigai", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(vaigaiRoom), femaleStudent, null, null, null, Collections.emptyMap()
        );

        assertTrue(results.isEmpty(), "Male hostel room must be HARD EXCLUDED for female student before scoring");
    }

    @Test
    @DisplayName("TEST 5: Male student + Pref Block = Vaigai -> Only eligible Vaigai rooms considered")
    void testMaleStudentPrefBlockVaigai() {
        Student maleStudent = new Student(1, 10, "STU1001", "Arun", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");

        Room vaigaiRoom = new Room(101, "V-101", "Vaigai", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);
        Room thamirabaraniRoom = new Room(201, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(vaigaiRoom, thamirabaraniRoom), maleStudent, "Vaigai", null, null, Collections.emptyMap()
        );

        assertEquals(1, results.size());
        assertEquals("V-101", results.get(0).getRoom().getRoomNumber());
    }

    @Test
    @DisplayName("TEST 6: Female student + Pref Block = Thamirabarani -> Only eligible Thamirabarani rooms considered")
    void testFemaleStudentPrefBlockThamirabarani() {
        Student femaleStudent = new Student(2, 11, "STU1002", "Priya", "priya@example.com", "9876543210", "FEMALE", "ECE", 1, "9876543210", "ACTIVE");

        Room vaigaiRoom = new Room(101, "V-101", "Vaigai", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);
        Room thamirabaraniRoom = new Room(201, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(vaigaiRoom, thamirabaraniRoom), femaleStudent, "Thamirabarani", null, null, Collections.emptyMap()
        );

        assertEquals(1, results.size());
        assertEquals("T-201", results.get(0).getRoom().getRoomNumber());
    }

    @Test
    @DisplayName("TEST 7: Male student + female hostel selected as preferred block -> 0 rooms returned")
    void testMaleStudentFemaleBlockSelectedReturnsZero() {
        Student maleStudent = new Student(1, 10, "STU1001", "Arun", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room thamirabaraniRoom = new Room(201, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(thamirabaraniRoom), maleStudent, "Thamirabarani", null, null, Collections.emptyMap()
        );

        assertTrue(results.isEmpty(), "Zero rooms should be returned when male student selects female hostel block");
    }

    @Test
    @DisplayName("TEST 8: Admin manually attempts incompatible assignment -> Backend rejects assignment")
    void testBackendRejectsIncompatibleAssignment() {
        int studentId = 1;
        int femaleRoomId = 201;

        Student maleStudent = new Student(studentId, 10, "STU1001", "Arun", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room femaleRoom = new Room(femaleRoomId, "T-201", "Thamirabarani", 2, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(maleStudent));
        when(roomRepository.findById(femaleRoomId)).thenReturn(Optional.of(femaleRoom));

        HostelException ex = assertThrows(HostelException.class, () -> roomService.allocateRoom(studentId, femaleRoomId, "Test Manual Allocation"));
        assertTrue(ex.getMessage().contains("Room assignment failed: The selected room is not compatible with the student's hostel eligibility"));
    }

    @Test
    @DisplayName("TEST 9: Full compatible room -> Excluded")
    void testFullCompatibleRoomExcluded() {
        Student maleStudent = new Student(1, 10, "STU1001", "Arun", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room fullVaigaiRoom = new Room(102, "V-102", "Vaigai", 1, 2, 2, RoomType.DOUBLE, RoomStatus.FULL, 6000.0);

        List<RoomRecommendationEngine.RecommendationResult> results = recommendationEngine.rankRooms(
                List.of(fullVaigaiRoom), maleStudent, null, null, null, Collections.emptyMap()
        );

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("TEST 10: Compatible available room -> Appears in recommendations")
    void testCompatibleAvailableRoomAppearsInRecommendations() throws HostelException {
        int studentId = 1;
        int roomId = 101;

        Student maleStudent = new Student(studentId, 10, "STU1001", "Arun", "arun@example.com", "9876543210", "MALE", "CSE", 2, "9876543210", "ACTIVE");
        Room vaigaiRoom = new Room(roomId, "V-101", "Vaigai", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 6000.0);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(maleStudent));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(vaigaiRoom));
        when(roomAllocationRepository.findByStudentIdAndStatus(studentId, "ACTIVE")).thenReturn(Optional.empty());
        when(roomAllocationRepository.save(any(RoomAllocation.class))).thenAnswer(i -> i.getArguments()[0]);
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArguments()[0]);

        RoomAllocation alloc = roomService.allocateRoom(studentId, roomId, "Valid Assignment");
        assertNotNull(alloc);
        assertEquals(studentId, alloc.getStudentId());
        assertEquals(roomId, alloc.getRoomId());
    }
}
