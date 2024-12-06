package com.project.parkingfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.project.parkingfinder.dto.ReservationDTO;
import com.project.parkingfinder.enums.ReservationStatus;
import com.project.parkingfinder.service.ReservationService;
import com.project.parkingfinder.service.ReservationService.UserReservationsResponse;

import jakarta.validation.Valid;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<String> updateReservation(
            @PathVariable("id") Long id,
            @RequestParam(value = "status", required = false) String status) {
        if (status != null && !ReservationStatus.isValid(status)) {
            throw new IllegalArgumentException("status không hợp lệ: " +status);
        }
        ReservationStatus reservationStatus  = ReservationStatus.valueOf(status.toUpperCase());
         reservationService.updateReservation(id, reservationStatus);
        return new ResponseEntity<>("Cập nhật trạng thái thành công", HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserReservationsResponse> getUserReservation(@PathVariable("userId") Long userId, @RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        UserReservationsResponse userReservations = reservationService.getUserReservations(userId, page, size);
        return new ResponseEntity<>(userReservations, HttpStatus.OK);
    }
    
}
