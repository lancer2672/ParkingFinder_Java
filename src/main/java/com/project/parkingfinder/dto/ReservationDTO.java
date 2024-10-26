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

    // Không nên dùng DTO làm 1 request struct
    @NotNull(message = "ID bãi đậu xe là bắt buộc")
    private Long parkingLotId;

    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc là bắt buộc")
    private LocalDateTime endTime;

    @NotNull(message = "Loại xe là bắt buộc")
    private String vehicleType;

    private Double totalPrice;

    private ReservationStatus status;
}
