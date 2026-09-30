package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.exception.InvalidLeaveDateException;
import com.hostel.model.LeaveRequest;
import com.hostel.model.enums.LeaveStatus;
import com.hostel.repository.LeaveRequestRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final StudentRepository studentRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository, StudentRepository studentRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public LeaveRequest applyForLeave(int studentId, LocalDate startDate, LocalDate endDate, String reason, String destination, String emergencyPhone) throws HostelException {
        if (startDate == null || endDate == null) {
            throw new InvalidLeaveDateException("Start date and End date must be provided.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidLeaveDateException("End date (" + endDate + ") cannot be before start date (" + startDate + ").");
        }

        LeaveRequest request = new LeaveRequest(null, studentId, startDate, endDate, reason, destination, emergencyPhone, LeaveStatus.PENDING);
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public boolean reviewLeaveRequest(int leaveId, LeaveStatus status, String adminRemarks) throws HostelException {
        LeaveRequest request = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new HostelException("Leave request with ID " + leaveId + " not found."));

        request.setStatus(status);
        request.setAdminRemarks(adminRemarks);
        request.setReviewedAt(LocalDateTime.now());
        leaveRequestRepository.save(request);
        return true;
    }

    @Transactional
    public boolean cancelLeaveRequest(int leaveId, int studentId) throws HostelException {
        LeaveRequest request = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new HostelException("Leave request with ID " + leaveId + " not found."));

        if (!request.getStudentId().equals(studentId)) {
            throw new HostelException("Unauthorized to cancel this leave request.");
        }

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new HostelException("Cannot cancel leave request in status: " + request.getStatus());
        }

        request.setStatus(LeaveStatus.CANCELLED);
        leaveRequestRepository.save(request);
        return true;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeavesByStudentId(int studentId) {
        return leaveRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaves() {
        List<LeaveRequest> list = leaveRequestRepository.findAllByOrderByCreatedAtDesc();
        for (LeaveRequest req : list) {
            studentRepository.findById(req.getStudentId()).ifPresent(s -> {
                req.setStudentName(s.getFullName());
                req.setStudentCode(s.getStudentCode());
            });
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Optional<LeaveRequest> getLeaveById(int leaveId) {
        return leaveRequestRepository.findById(leaveId);
    }
}
