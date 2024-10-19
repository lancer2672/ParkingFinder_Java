package com.project.parkingfinder.dto;

import com.project.parkingfinder.enums.VehicleTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotDTO {
    private Long id;
    private Long parkingLotId;
    private VehicleTypeEnum type;
    private Double price;
    private Integer totalSlots;
    private Integer activeSlots;
}
