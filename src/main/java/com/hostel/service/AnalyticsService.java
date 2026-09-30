package com.hostel.service;

import com.hostel.model.Complaint;
import com.hostel.model.Fee;
import com.hostel.model.LeaveRequest;
import com.hostel.model.Room;
import com.hostel.model.Student;
import com.hostel.model.enums.ComplaintPriority;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.model.enums.FeeStatus;
import com.hostel.model.enums.LeaveStatus;
import com.hostel.model.enums.RoomStatus;
import com.hostel.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ComplaintRepository complaintRepository;
    private final FeeRepository feeRepository;

    public AnalyticsService(StudentRepository studentRepository, RoomRepository roomRepository, LeaveRequestRepository leaveRequestRepository, ComplaintRepository complaintRepository, FeeRepository feeRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.complaintRepository = complaintRepository;
        this.feeRepository = feeRepository;
    }

    public static class SystemDashboardStats extends com.hostel.service.SystemDashboardStats {
    }

    @Transactional(readOnly = true)
    public SystemDashboardStats getDashboardStats() {
        SystemDashboardStats stats = new SystemDashboardStats();

        List<Student> students = studentRepository.findAll();
        stats.totalStudents = students.size();

        List<Room> rooms = roomRepository.findAll();
        stats.totalRooms = rooms.size();
        stats.totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        stats.totalOccupancy = rooms.stream().mapToInt(Room::getCurrentOccupancy).sum();
        stats.occupiedRooms = (int) rooms.stream().filter(r -> r.getCurrentOccupancy() > 0).count();
        stats.availableRooms = (int) rooms.stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();
        stats.partiallyOccupiedRooms = (int) rooms.stream().filter(r -> r.getStatus() == RoomStatus.PARTIALLY_OCCUPIED).count();
        stats.fullRooms = (int) rooms.stream().filter(r -> r.getStatus() == RoomStatus.FULL).count();
        stats.maintenanceRooms = (int) rooms.stream().filter(r -> r.getStatus() == RoomStatus.MAINTENANCE).count();
        stats.occupancyPercentage = (stats.totalCapacity > 0) ? ((double) stats.totalOccupancy / stats.totalCapacity) * 100.0 : 0.0;

        List<LeaveRequest> leaves = leaveRequestRepository.findAll();
        stats.pendingLeaves = leaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count();

        List<Complaint> complaints = complaintRepository.findAll();
        stats.openComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.OPEN || c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        stats.criticalComplaints = complaints.stream().filter(c -> c.getCalculatedPriority() == ComplaintPriority.CRITICAL && c.getStatus() != ComplaintStatus.RESOLVED && c.getStatus() != ComplaintStatus.CLOSED).count();

        List<Fee> fees = feeRepository.findAll();
        stats.totalUnpaidFees = fees.stream()
                .filter(f -> f.getStatus() != FeeStatus.PAID)
                .mapToDouble(Fee::getRemainingBalance)
                .sum();

        return stats;
    }
}
