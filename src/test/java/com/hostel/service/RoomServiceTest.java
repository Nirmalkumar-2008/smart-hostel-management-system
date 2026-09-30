package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.model.Room;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.WaitingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAllocationRepository roomAllocationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private WaitingListRepository waitingListRepository;

    @InjectMocks
    private RoomService roomService;

    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        sampleRoom = new Room(1, "A101", "Block A", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 5000.0, "Standard Room");
    }

    @Test
    @DisplayName("1. Add Room successfully")
    void testAddRoomSuccess() {
        when(roomRepository.findByRoomNumber("A101")).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenReturn(sampleRoom);

        Room created = roomService.addRoom("A101", "Block A", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 5000.0, "Standard Room");

        assertNotNull(created);
        assertEquals("A101", created.getRoomNumber());
        assertEquals(RoomStatus.AVAILABLE, created.getStatus());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    @DisplayName("2. View Room by ID successfully")
    void testGetRoomByIdSuccess() throws HostelException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(sampleRoom));

        Room found = roomService.getRoomById(1);

        assertNotNull(found);
        assertEquals("A101", found.getRoomNumber());
    }

    @Test
    @DisplayName("3. Edit Room successfully")
    void testUpdateRoomSuccess() throws HostelException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(sampleRoom));
        when(roomRepository.findByRoomNumber("A102")).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenReturn(sampleRoom);

        Room updated = roomService.updateRoom(1, "A102", "Block A", 1, 4, 2, RoomType.FOUR_SHARING, RoomStatus.PARTIALLY_OCCUPIED, 6000.0, "Updated Room");

        assertNotNull(updated);
        assertEquals("A102", sampleRoom.getRoomNumber());
        assertEquals(4, sampleRoom.getCapacity());
        assertEquals(2, sampleRoom.getCurrentOccupancy());
        assertEquals(RoomStatus.PARTIALLY_OCCUPIED, sampleRoom.getStatus());
        verify(roomRepository).save(sampleRoom);
    }

    @Test
    @DisplayName("4. Delete unoccupied room successfully")
    void testDeleteUnoccupiedRoomSuccess() throws HostelException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(sampleRoom));
        when(roomAllocationRepository.findByRoomIdAndStatus(1, "ACTIVE")).thenReturn(Collections.emptyList());

        roomService.deleteRoom(1);

        verify(roomRepository).delete(sampleRoom);
    }

    @Test
    @DisplayName("5. Duplicate Room Number rejected")
    void testDuplicateRoomNumberRejected() {
        when(roomRepository.findByRoomNumber("A101")).thenReturn(Optional.of(sampleRoom));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            roomService.addRoom("A101", "Block A", 1, 2, 0, RoomType.DOUBLE, RoomStatus.AVAILABLE, 5000.0, "Desc")
        );

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("6. Invalid capacity rejected")
    void testInvalidCapacityRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            roomService.addRoom("B202", "Block B", 2, 0, 0, RoomType.SINGLE, RoomStatus.AVAILABLE, 4000.0, "Desc")
        );

        assertTrue(ex.getMessage().contains("Capacity must be greater than 0"));
    }

    @Test
    @DisplayName("7. Occupancy greater than capacity rejected")
    void testOccupancyGreaterThanCapacityRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            roomService.addRoom("B202", "Block B", 2, 2, 3, RoomType.DOUBLE, RoomStatus.AVAILABLE, 4000.0, "Desc")
        );

        assertTrue(ex.getMessage().contains("cannot exceed Capacity"));
    }

    @Test
    @DisplayName("8. Delete occupied room rejected")
    void testDeleteOccupiedRoomRejected() {
        sampleRoom.setCurrentOccupancy(1);
        when(roomRepository.findById(1)).thenReturn(Optional.of(sampleRoom));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            roomService.deleteRoom(1)
        );

        assertEquals("Room cannot be deleted because students are currently assigned to this room.", ex.getMessage());
        verify(roomRepository, never()).delete(any(Room.class));
    }

    @Test
    @DisplayName("9. Search and Filter Rooms")
    void testSearchAndFilterRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(sampleRoom));

        List<Room> result = roomService.searchAndFilterRooms("A101", 1, "DOUBLE", "AVAILABLE");

        assertEquals(1, result.size());
        assertEquals("A101", result.get(0).getRoomNumber());
    }

    @Test
    @DisplayName("10. Room Status Auto-Computation for FULL and PARTIALLY_OCCUPIED")
    void testRoomStatusAutoComputation() throws HostelException {
        when(roomRepository.findById(1)).thenReturn(Optional.of(sampleRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(sampleRoom);

        // Edit to full occupancy (2 / 2)
        roomService.updateRoom(1, "A101", "Block A", 1, 2, 2, RoomType.DOUBLE, RoomStatus.AVAILABLE, 5000.0, "Desc");
        assertEquals(RoomStatus.FULL, sampleRoom.getStatus());

        // Edit to partial occupancy (1 / 2)
        roomService.updateRoom(1, "A101", "Block A", 1, 2, 1, RoomType.DOUBLE, RoomStatus.AVAILABLE, 5000.0, "Desc");
        assertEquals(RoomStatus.PARTIALLY_OCCUPIED, sampleRoom.getStatus());
    }
}
