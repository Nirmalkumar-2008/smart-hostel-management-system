package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.model.MedicalRequest;
import com.hostel.model.enums.MedicalStatus;
import com.hostel.model.enums.MedicalUrgency;
import com.hostel.repository.MedicalRequestRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalService {

    private final MedicalRequestRepository medicalRequestRepository;
    private final StudentRepository studentRepository;

    public MedicalService(MedicalRequestRepository medicalRequestRepository, StudentRepository studentRepository) {
        this.medicalRequestRepository = medicalRequestRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public MedicalRequest submitMedicalRequest(int studentId, String requestType, String description, MedicalUrgency urgency) {
        MedicalRequest req = new MedicalRequest(null, studentId, requestType, description, urgency, MedicalStatus.PENDING);
        return medicalRequestRepository.save(req);
    }

    @Transactional
    public boolean updateMedicalStatus(int requestId, MedicalStatus newStatus, String doctorAssigned, String doctorNotes) throws HostelException {
        MedicalRequest req = medicalRequestRepository.findById(requestId)
                .orElseThrow(() -> new HostelException("Medical request with ID " + requestId + " not found."));

        req.setStatus(newStatus);
        if (doctorAssigned != null && !doctorAssigned.trim().isEmpty()) req.setDoctorAssigned(doctorAssigned.trim());
        if (doctorNotes != null && !doctorNotes.trim().isEmpty()) req.setDoctorNotes(doctorNotes.trim());
        medicalRequestRepository.save(req);
        return true;
    }

    @Transactional(readOnly = true)
    public List<MedicalRequest> getMedicalRequestsByStudentId(int studentId) {
        return medicalRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<MedicalRequest> getAllMedicalRequests() {
        List<MedicalRequest> list = medicalRequestRepository.findAllByOrderByCreatedAtDesc();
        for (MedicalRequest req : list) {
            studentRepository.findById(req.getStudentId()).ifPresent(s -> {
                req.setStudentName(s.getFullName());
                req.setStudentCode(s.getStudentCode());
            });
        }
        return list;
    }
}
