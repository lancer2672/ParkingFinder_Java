package com.project.parkingfinder.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.parkingfinder.enums.ReservationStatus;

import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.model.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ReservationDTO {
    
    private Long id;
    //  NULL nếu khách vãng lai
    private Long userId;

    // Không nên dùng DTO làm 1 request struct
    @NotNull(message = "ID bãi đậu xe là bắt buộc")
    private Long parkingLotId;

    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @NotNull(message = "Loại xe là bắt buộc")
    private String vehicleType;

    private Double totalPrice;

    private ReservationStatus status;
//    @JsonIgnore
    private Integer cancelMinute;
    private  Payment payment;
    private ParkingLot parkingLot;

}
