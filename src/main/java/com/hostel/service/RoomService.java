package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.exception.RoomFullException;
import com.hostel.model.Room;
import com.hostel.model.RoomAllocation;
import com.hostel.model.Student;
import com.hostel.model.WaitingListEntry;
import com.hostel.model.enums.RoomStatus;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import com.hostel.repository.WaitingListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final StudentRepository studentRepository;
    private final WaitingListRepository waitingListRepository;

    public RoomService(RoomRepository roomRepository, RoomAllocationRepository roomAllocationRepository, StudentRepository studentRepository, WaitingListRepository waitingListRepository) {
        this.roomRepository = roomRepository;
        this.roomAllocationRepository = roomAllocationRepository;
        this.studentRepository = studentRepository;
        this.waitingListRepository = waitingListRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(int id) throws HostelException {
        return roomRepository.findById(id)
                .orElseThrow(() -> new HostelException("Room with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE || (r.getStatus() == RoomStatus.PARTIALLY_OCCUPIED && !r.isFull()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Room> searchAndFilterRooms(String query, Integer floor, String roomTypeStr, String statusStr) {
        List<Room> rooms = getAllRooms();

        RoomType filterType = null;
        if (roomTypeStr != null && !roomTypeStr.trim().isEmpty()) {
            try {
                filterType = RoomType.valueOf(roomTypeStr.trim().toUpperCase());
            } catch (Exception ignored) {}
        }

        RoomStatus filterStatus = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                filterStatus = RoomStatus.valueOf(statusStr.trim().toUpperCase());
            } catch (Exception ignored) {}
        }

        final RoomType finalType = filterType;
        final RoomStatus finalStatus = filterStatus;
        final String q = (query != null) ? query.trim().toLowerCase() : "";

        return rooms.stream()
                .filter(r -> {
                    if (q.isEmpty()) return true;
                    return (r.getRoomNumber() != null && r.getRoomNumber().toLowerCase().contains(q))
                            || (r.getBlock() != null && r.getBlock().toLowerCase().contains(q))
                            || (r.getDescription() != null && r.getDescription().toLowerCase().contains(q));
                })
                .filter(r -> (floor == null || floor <= 0 || r.getFloor().equals(floor)))
                .filter(r -> (finalType == null || r.getRoomType() == finalType))
                .filter(r -> (finalStatus == null || r.getStatus() == finalStatus))
                .collect(Collectors.toList());
    }

    @Transactional
    public Room addRoom(String roomNumber, String block, int floor, int capacity, RoomType roomType, double rent) {
        return addRoom(roomNumber, block, floor, capacity, 0, roomType, RoomStatus.AVAILABLE, rent, null);
    }

    @Transactional
    public Room addRoom(String roomNumber, String block, int floor, int capacity, int currentOccupancy, RoomType roomType, RoomStatus status, double rent, String description) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room Number is required.");
        }
        String trimmedRoomNum = roomNumber.trim();
        if (roomRepository.findByRoomNumber(trimmedRoomNum).isPresent()) {
            throw new IllegalArgumentException("Room Number '" + trimmedRoomNum + "' already exists.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        if (currentOccupancy < 0) {
            throw new IllegalArgumentException("Current Occupancy cannot be negative.");
        }
        if (currentOccupancy > capacity) {
            throw new IllegalArgumentException("Current Occupancy cannot exceed Capacity (" + capacity + ").");
        }

        Room room = new Room(null, trimmedRoomNum, (block == null || block.trim().isEmpty()) ? "A" : block.trim(), floor, capacity, currentOccupancy, roomType, (status != null) ? status : RoomStatus.AVAILABLE, rent, description);
        room.updateComputedStatus();
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(int id, String roomNumber, String block, int floor, int capacity, int currentOccupancy, RoomType roomType, RoomStatus status, double rent, String description) throws HostelException {
        Room room = getRoomById(id);

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room Number is required.");
        }
        String trimmedRoomNum = roomNumber.trim();
        if (!trimmedRoomNum.equalsIgnoreCase(room.getRoomNumber())) {
            Optional<Room> existing = roomRepository.findByRoomNumber(trimmedRoomNum);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Room Number '" + trimmedRoomNum + "' already exists.");
            }
            room.setRoomNumber(trimmedRoomNum);
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        if (currentOccupancy < 0) {
            throw new IllegalArgumentException("Current Occupancy cannot be negative.");
        }
        if (currentOccupancy > capacity) {
            throw new IllegalArgumentException("Current Occupancy cannot exceed Capacity (" + capacity + ").");
        }

        room.setBlock((block == null || block.trim().isEmpty()) ? "A" : block.trim());
        room.setFloor(floor);
        room.setCapacity(capacity);
        room.setCurrentOccupancy(currentOccupancy);
        if (roomType != null) room.setRoomType(roomType);
        if (status != null) room.setStatus(status);
        room.setBaseMonthlyRent(rent);
        room.setDescription(description);

        room.updateComputedStatus();
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(int id) throws HostelException {
        Room room = getRoomById(id);

        // Check if room has active occupants or active room allocations
        List<RoomAllocation> activeAllocations = roomAllocationRepository.findByRoomIdAndStatus(id, "ACTIVE");
        if ((room.getCurrentOccupancy() != null && room.getCurrentOccupancy() > 0) || !activeAllocations.isEmpty()) {
            throw new IllegalArgumentException("Room cannot be deleted because students are currently assigned to this room.");
        }

        roomRepository.delete(room);
    }

    @Transactional(readOnly = true)
    public List<Student> getAssignedStudentsForRoom(int roomId) {
        List<RoomAllocation> allocs = roomAllocationRepository.findByRoomIdAndStatus(roomId, "ACTIVE");
        List<Student> students = new ArrayList<>();
        for (RoomAllocation alloc : allocs) {
            studentRepository.findById(alloc.getStudentId()).ifPresent(students::add);
        }
        return students;
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctBlocks() {
        return roomRepository.findDistinctBlocks();
    }

    public boolean isGenderCompatible(Student student, Room room, List<Student> occupants) {
        if (student == null || student.getGender() == null || room == null) {
            return true;
        }
        String sGender = student.getGender().trim().toUpperCase();

        if (occupants != null && !occupants.isEmpty()) {
            for (Student occ : occupants) {
                if (occ.getGender() != null && !occ.getGender().trim().equalsIgnoreCase(sGender)) {
                    return false;
                }
            }
        }

        if (room.getGender() != null && !room.getGender().trim().isEmpty() && !room.getGender().trim().equalsIgnoreCase("ANY") && !room.getGender().trim().equalsIgnoreCase("COED")) {
            if (!room.getGender().trim().equalsIgnoreCase(sGender)) {
                return false;
            }
        }

        String block = (room.getBlock() != null) ? room.getBlock().trim().toLowerCase() : "";
        if (block.contains("vaigai") || block.contains("boys") || block.contains("mens") || (block.contains("male") && !block.contains("female"))) {
            if (!sGender.contains("MALE") || sGender.contains("FEMALE")) {
                return false;
            }
        } else if (block.contains("thamirabarani") || block.contains("girls") || block.contains("female") || block.contains("ladies") || block.contains("womens")) {
            if (!sGender.contains("FEMALE")) {
                return false;
            }
        }

        return true;
    }

    @Transactional
    public RoomAllocation allocateRoom(int studentId, int roomId, String notes) throws HostelException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new HostelException("Student with ID " + studentId + " not found."));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new HostelException("Room with ID " + roomId + " not found."));

        if (room.isFull() || room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new RoomFullException("Room " + room.getRoomNumber() + " is full or under maintenance.");
        }

        List<Student> occupants = getAssignedStudentsForRoom(roomId);
        if (!isGenderCompatible(student, room, occupants)) {
            throw new HostelException("Room assignment failed: The selected room is not compatible with the student's hostel eligibility.");
        }

        // Deallocate existing active allocation if any
        roomAllocationRepository.findByStudentIdAndStatus(studentId, "ACTIVE").ifPresent(alloc -> {
            alloc.setStatus("DEALLOCATED");
            alloc.setDeallocatedDate(LocalDate.now());
            roomAllocationRepository.save(alloc);

            roomRepository.findById(alloc.getRoomId()).ifPresent(oldRoom -> {
                oldRoom.setCurrentOccupancy(Math.max(0, oldRoom.getCurrentOccupancy() - 1));
                oldRoom.updateComputedStatus();
                roomRepository.save(oldRoom);
            });
        });

        // Atomic allocation
        RoomAllocation newAlloc = new RoomAllocation(null, student.getId(), room.getId(), LocalDate.now(), "ACTIVE", notes);
        newAlloc = roomAllocationRepository.save(newAlloc);

        room.setCurrentOccupancy((room.getCurrentOccupancy() == null ? 0 : room.getCurrentOccupancy()) + 1);
        room.updateComputedStatus();
        roomRepository.save(room);

        // Update waiting list status if applicable
        waitingListRepository.findByStudentId(studentId).forEach(entry -> {
            if ("WAITING".equals(entry.getStatus())) {
                entry.setStatus("ALLOCATED");
                waitingListRepository.save(entry);
            }
        });

        return newAlloc;
    }

    @Transactional
    public boolean deallocateStudent(int studentId) throws HostelException {
        RoomAllocation alloc = roomAllocationRepository.findByStudentIdAndStatus(studentId, "ACTIVE")
                .orElseThrow(() -> new HostelException("No active room allocation found for student ID " + studentId));

        alloc.setStatus("DEALLOCATED");
        alloc.setDeallocatedDate(LocalDate.now());
        roomAllocationRepository.save(alloc);

        Room room = roomRepository.findById(alloc.getRoomId())
                .orElseThrow(() -> new HostelException("Room not found."));

        room.setCurrentOccupancy(Math.max(0, (room.getCurrentOccupancy() == null ? 0 : room.getCurrentOccupancy()) - 1));
        room.updateComputedStatus();
        roomRepository.save(room);
        return true;
    }

    @Transactional(readOnly = true)
    public RoomAllocation getActiveAllocationForStudent(int studentId) {
        return roomAllocationRepository.findByStudentIdAndStatus(studentId, "ACTIVE").orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Student> getRoommatesForStudent(int studentId) {
        RoomAllocation alloc = getActiveAllocationForStudent(studentId);
        if (alloc == null) return new ArrayList<>();

        List<RoomAllocation> roomAllocs = roomAllocationRepository.findByRoomIdAndStatus(alloc.getRoomId(), "ACTIVE");
        List<Student> roommates = new ArrayList<>();
        for (RoomAllocation a : roomAllocs) {
            if (!a.getStudentId().equals(studentId)) {
                studentRepository.findById(a.getStudentId()).ifPresent(roommates::add);
            }
        }
        return roommates;
    }

    @Transactional(readOnly = true)
    public List<RoomAllocation> getAllAllocations() {
        List<RoomAllocation> list = roomAllocationRepository.findAll();
        for (RoomAllocation a : list) {
            studentRepository.findById(a.getStudentId()).ifPresent(s -> a.setStudentName(s.getFullName()));
            roomRepository.findById(a.getRoomId()).ifPresent(r -> a.setRoomNumber(r.getRoomNumber()));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<WaitingListEntry> getWaitingList() {
        List<WaitingListEntry> list = waitingListRepository.findByStatusOrderByPriorityScoreDescRequestDateAsc("WAITING");
        for (WaitingListEntry w : list) {
            studentRepository.findById(w.getStudentId()).ifPresent(s -> {
                w.setStudentName(s.getFullName());
                w.setStudentCode(s.getStudentCode());
            });
        }
        return list;
    }

    @Transactional
    public WaitingListEntry addToWaitingList(int studentId, String preferredBlock, RoomType preferredRoomType) {
        WaitingListEntry entry = new WaitingListEntry(null, studentId, preferredBlock, preferredRoomType, LocalDate.now(), 50, "WAITING");
        return waitingListRepository.save(entry);
    }
}
