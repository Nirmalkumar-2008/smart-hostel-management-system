package com.hostel.service;

import com.hostel.algorithm.ComplaintPriorityService;
import com.hostel.exception.HostelException;
import com.hostel.model.Complaint;
import com.hostel.model.enums.ComplaintCategory;
import com.hostel.model.enums.ComplaintPriority;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.repository.ComplaintRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final ComplaintPriorityService complaintPriorityService;

    public ComplaintService(ComplaintRepository complaintRepository, StudentRepository studentRepository, ComplaintPriorityService complaintPriorityService) {
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
        this.complaintPriorityService = complaintPriorityService;
    }

    @Transactional
    public Complaint submitComplaint(int studentId, ComplaintCategory category, String title, String description, String location, int affectedCount, int severityScore) {
        ComplaintPriority priority = complaintPriorityService.calculatePriority(category, severityScore, affectedCount, 0);

        Complaint complaint = new Complaint(null, studentId, category, title, description, location, affectedCount, severityScore, priority, ComplaintStatus.OPEN);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public boolean updateComplaintStatus(int complaintId, ComplaintStatus newStatus, String resolutionNotes) throws HostelException {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new HostelException("Complaint with ID " + complaintId + " not found."));

        complaint.setStatus(newStatus);
        if (resolutionNotes != null && !resolutionNotes.trim().isEmpty()) {
            complaint.setResolutionNotes(resolutionNotes.trim());
        }
        if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.CLOSED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        complaintRepository.save(complaint);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsByStudentId(int studentId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        List<Complaint> list = complaintRepository.findAllByOrderByCreatedAtDesc();
        for (Complaint c : list) {
            studentRepository.findById(c.getStudentId()).ifPresent(s -> {
                c.setStudentName(s.getFullName());
                c.setStudentCode(s.getStudentCode());
            });
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Optional<Complaint> getComplaintById(int complaintId) {
        return complaintRepository.findById(complaintId);
    }
}
