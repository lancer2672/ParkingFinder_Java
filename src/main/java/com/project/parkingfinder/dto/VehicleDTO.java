package com.project.parkingfinder.dto;

import com.project.parkingfinder.enums.VehicleTypeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
//1 là dùng interface Projection
//2 là dùng Object rồi map qua DTO
//3 là dùng query constructor trong query
public class VehicleDTO {
    private Long parkingLotId;
    private Double price;
    @Enumerated(EnumType.STRING) 
    private VehicleTypeEnum type;   
    private Long parkingSlotId;
    private Integer totalSlots;
    private Integer activeSlots;
}
