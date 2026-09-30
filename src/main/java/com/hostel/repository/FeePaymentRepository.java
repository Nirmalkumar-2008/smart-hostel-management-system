package com.hostel.repository;

import com.hostel.model.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Integer> {
    List<FeePayment> findByFeeId(Integer feeId);
}
