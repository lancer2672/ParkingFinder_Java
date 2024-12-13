package com.project.parkingfinder.controller;

import com.project.parkingfinder.service.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.parkingfinder.dto.PaymentDTO;
import com.project.parkingfinder.model.Payment;
import com.project.parkingfinder.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Autowired
    private final SocketService socketService;

    public PaymentController(PaymentService paymentService, SocketService socketService) {
        this.paymentService = paymentService;
        this.socketService = socketService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        Payment createdPayment = paymentService.createPayment(paymentDTO);
        if(paymentDTO.getUserId() != null){
            socketService.emitPaymentMessage(paymentDTO.getUserId().toString(),createdPayment.getPaymentStatus().toString(), createdPayment.getAmount(), paymentDTO.getReservationId().toString());
        }
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }
    @GetMapping("/vn-pay")
    public ResponseEntity<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseEntity<>(paymentService.createVnPayPayment(request), HttpStatus.OK );
    }
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        PaymentDTO.VNPayResponse res = new PaymentDTO.VNPayResponse(status,status.equals("00") ? "Success" : "Failed","");
        return new ResponseEntity<>(res, status.equals("00") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
