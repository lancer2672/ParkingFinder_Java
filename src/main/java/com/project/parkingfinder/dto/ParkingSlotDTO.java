package com.project.parkingfinder.dto;

import com.project.parkingfinder.enums.ParkingSlotStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotDTO {
    private Long id;
    private String slotNumber;
    private ParkingSlotStatus status;
    private Long parkingLotId;
}
