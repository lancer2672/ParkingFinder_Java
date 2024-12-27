package com.project.parkingfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.service.SocketService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/socket-test")
@Slf4j
public class SocketController {

    @Autowired
    private SocketService socketService;

    @PostMapping("/payment")
    public ResponseEntity<String> testPaymentMessage(
            @RequestParam String userId,
            @RequestParam String paymentStatus,
            @RequestParam Double amount,
            @RequestParam String reservationId
    ) {
        try {
            socketService.emitPaymentMessage(userId, paymentStatus,amount,reservationId);
            return ResponseEntity.ok("Payment message sent successfully");
        } catch (Exception e) {
            log.error("Error sending payment message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send payment message");
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> testCancelMessage(
            @RequestParam String userId,
            @RequestParam String reservationId
    ) {
        try {
            socketService.emitCancelMessage(userId, reservationId);
            return ResponseEntity.ok("Cancel message sent successfully");
        } catch (Exception e) {
            log.error("Error sending cancel message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send cancel message");
        }
    }

    @PostMapping("/update-reservation")
    public ResponseEntity<String> testUpdateReservationMessage(
            @RequestParam String userId,
            @RequestParam String reservationId,
            @RequestParam String status,
            @RequestParam Double price,
            @RequestParam String parkingLotID

    ) {
        try {
            socketService.emitUpdateStatusMsg(userId, reservationId, status,price,parkingLotID);
            return ResponseEntity.ok("Reservation update message sent successfully");
        } catch (Exception e) {
            log.error("Error sending reservation update message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send reservation update message");
        }
    }
}