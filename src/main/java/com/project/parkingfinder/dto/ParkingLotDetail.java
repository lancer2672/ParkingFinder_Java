package com.project.parkingfinder.dto;

import java.time.LocalTime;
import java.util.List;

import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.service.LocationService;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParkingLotDetail {
    private Long id;
    private Long ownerId;
    private String provinceId;
    private String provinceName;
    private String districtId; 
    private String districtName;
    private String wardId;
    private String wardName;
    private String name;
    private String address;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
    private LocalTime openHour;
    private LocalTime closeHour;
    private ParkingLotStatus status;
    private String imageUrls;
    private User owner;
    private List<VehicleDTO> vehicles;

    public ParkingLotDetail(Long id, Long ownerId, String provinceId, 
             String districtId, 
            String wardId,  String name, String address,
       Double latitude, Double longitude,
            LocalTime openHour, LocalTime closeHour, ParkingLotStatus status,
            String imageUrls, User owner) {
        this.id = id;
        this.ownerId = ownerId;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.status = status;
        this.imageUrls = imageUrls;
        this.owner = owner;
    }
       public void fetchLocationNames() {
        this.provinceName = LocationService.getProvinceName(this.provinceId);
        this.districtName = LocationService.getDistrictName(this.provinceId, this.districtId);
        this.wardName = LocationService.getWardName(this.districtId, this.wardId);
    }
}
