package com.project.parkingfinder.dto;

import java.time.LocalDateTime;

import com.project.parkingfinder.enums.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationDTO {
    
    private Long id;
    //  NULL nếu khách vãng lai
    private Long userId;

    @NotNull(message = "Parking lot ID is required")
    private Long parkingLotId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Vehicle type is required")
    private String vehicleType;

    private Double totalPrice;

    private ReservationStatus status;
}
