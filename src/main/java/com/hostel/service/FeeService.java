package com.hostel.service;

import com.hostel.exception.HostelException;
import com.hostel.model.Fee;
import com.hostel.model.FeePayment;
import com.hostel.model.enums.FeeStatus;
import com.hostel.model.enums.FeeType;
import com.hostel.model.enums.PaymentMethod;
import com.hostel.repository.FeePaymentRepository;
import com.hostel.repository.FeeRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeeService {

    private final FeeRepository feeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    public FeeService(FeeRepository feeRepository, FeePaymentRepository feePaymentRepository, StudentRepository studentRepository) {
        this.feeRepository = feeRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public Fee createFeeRecord(String feeCode, int studentId, FeeType feeType, double amount, LocalDate dueDate, String academicTerm) {
        Fee fee = new Fee(null, feeCode, studentId, feeType, amount, dueDate, FeeStatus.UNPAID, academicTerm);
        return feeRepository.save(fee);
    }

    @Transactional
    public FeePayment recordPayment(int feeId, double amountPaid, PaymentMethod method, String refNumber, String notes) throws HostelException {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new HostelException("Fee record with ID " + feeId + " not found."));

        FeePayment payment = new FeePayment(null, fee.getId(), amountPaid, LocalDate.now(), method, refNumber, notes);
        payment = feePaymentRepository.save(payment);

        List<FeePayment> payments = feePaymentRepository.findByFeeId(fee.getId());
        double totalPaid = payments.stream().mapToDouble(FeePayment::getAmountPaid).sum();

        if (totalPaid >= fee.getTotalAmount()) {
            fee.setStatus(FeeStatus.PAID);
        } else if (totalPaid > 0) {
            fee.setStatus(FeeStatus.PARTIALLY_PAID);
        }
        feeRepository.save(fee);

        return payment;
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByStudentId(int studentId) {
        List<Fee> fees = feeRepository.findByStudentId(studentId);
        for (Fee f : fees) {
            List<FeePayment> payments = feePaymentRepository.findByFeeId(f.getId());
            double totalPaid = payments.stream().mapToDouble(FeePayment::getAmountPaid).sum();
            f.setTotalPaidAmount(totalPaid);
        }
        return fees;
    }

    @Transactional(readOnly = true)
    public List<Fee> getAllFees() {
        List<Fee> fees = feeRepository.findAllByOrderByDueDateAsc();
        for (Fee f : fees) {
            List<FeePayment> payments = feePaymentRepository.findByFeeId(f.getId());
            double totalPaid = payments.stream().mapToDouble(FeePayment::getAmountPaid).sum();
            f.setTotalPaidAmount(totalPaid);
            studentRepository.findById(f.getStudentId()).ifPresent(s -> {
                f.setStudentName(s.getFullName());
                f.setStudentCode(s.getStudentCode());
            });
        }
        return fees;
    }
}
