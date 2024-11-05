package com.project.parkingfinder.service;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.PaymentDTO;
import com.project.parkingfinder.enums.PaymentStatus;
import com.project.parkingfinder.model.Payment;
import com.project.parkingfinder.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment convertPaymentDTOToPayment(PaymentDTO paymentDTO, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setReservationId(paymentDTO.getReservationId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentStatus(status);
        return payment;
    }

    public Payment createPayment(PaymentDTO paymentDTO) {
  
            Payment existsPayment = paymentRepository.findByReservationIdAndPaymentStatus(paymentDTO.getReservationId(), PaymentStatus.COMPLETED).orElse(null);
            if (existsPayment != null) {
                throw new IllegalArgumentException("This reservation is already paid");
            }
            
            Payment payment = convertPaymentDTOToPayment(paymentDTO, PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            return payment;
       
    }
}
