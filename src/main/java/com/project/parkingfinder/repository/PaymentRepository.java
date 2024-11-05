package com.project.parkingfinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.enums.PaymentStatus;
import com.project.parkingfinder.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Optional<Payment> findByReservationIdAndPaymentStatus(Long reservationId, PaymentStatus status);

}
