
package com.project.parkingfinder.dto;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

        @NotNull(message = "Owner ID is required")
        private Long ownerId;

        @NotNull(message = "Province ID is required")
        private Long provinceId;

        @NotNull(message = "District ID is required")
        private Long districtId;

        @NotNull(message = "Ward ID is required")
        private Long wardId;

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Address is required")
        private String address;

        @NotNull(message = "Capacity is required")
        private int capacity;

        @NotNull(message = "Latitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;

        @NotBlank(message = "Open hour is required")
        private LocalTime openHour;

        @NotBlank(message = "Close hour is required")
        private LocalTime closeHour;

        private ParkingLotStatus status;

        private List<String> images;

        @JsonIgnore
        private List<MultipartFile> imageFiles;

        // Constructor for creating a new ParkingLotDTO with image files
        public ParkingLotDTO(String name, String address,  double latitude,int capacity, double longitude,
                             LocalTime openHour, LocalTime closeHour, List<MultipartFile> imageFiles,
                             Long ownerId, Long provinceId, Long districtId, Long wardId) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.openHour = openHour;
            this.closeHour = closeHour;
            this.imageFiles = imageFiles;
            this.capacity = capacity;
            this.status = ParkingLotStatus.PENDING; // Set default status to PENDING
            this.ownerId = ownerId;
            this.provinceId = provinceId;
            this.districtId = districtId;
            this.wardId = wardId;
        }

    }

