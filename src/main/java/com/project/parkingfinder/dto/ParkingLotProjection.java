package com.project.parkingfinder.dto;

import java.time.LocalTime;

import com.project.parkingfinder.enums.ParkingLotStatus;

public interface ParkingLotProjection {
    Long getId();
    Long getOwnerId();
    String getProvinceId();
    String getDistrictId();
    String getWardId();
    String getName();
    String getAddress();
    Double getLongitude();
    Double getLatitude();
    LocalTime getOpenHour();
    ParkingLotStatus getStatus();
    LocalTime getCloseHour();
    Long getTotalParkingSlots();
    String getImageUrl();
}