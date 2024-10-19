package com.project.parkingfinder.dto;

import com.project.parkingfinder.enums.ParkingLotStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotDTO {
    private Long id;
    private String name;
    private String address;
    private int capacity;
    private double latitude;
    private double longitude;
    private ParkingLotStatus status;
    private String imageUrl;
//    private String imageUrls
//    private String avatar
}
