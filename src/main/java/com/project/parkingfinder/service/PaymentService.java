package com.project.parkingfinder.service;


import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.config.VNPayConfig;
import com.project.parkingfinder.dto.PaymentDTO;
import com.project.parkingfinder.enums.PaymentStatus;
import com.project.parkingfinder.model.Payment;
import com.project.parkingfinder.repository.PaymentRepository;
import com.project.parkingfinder.util.VNPayUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final VNPayConfig vnPayConfig;
    
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    @Autowired
    public PaymentService(PaymentRepository paymentRepository, VNPayConfig vnPayConfig) {
        this.paymentRepository = paymentRepository;
        this.vnPayConfig = vnPayConfig;
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
