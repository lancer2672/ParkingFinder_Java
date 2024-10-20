package com.project.parkingfinder.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.parkingfinder.controller.FileController;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.service.LocationService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String provinceId;
    private String provinceName;

    @NotNull(message = "District ID is required")
    private String districtId;
    private String districtName;

    @NotNull(message = "Ward ID is required")
    private String wardId;
    private String wardName;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

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

    public void setImages(List<String> images) {
        this.images = images.stream()
            .map(image -> FileController.SERVER_URL + "/api/files/stream/" + image)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    private List<MultipartFile> imageFiles;

    // Constructor for creating a new ParkingLotDTO with image files
    public ParkingLotDTO(String name, String address, double latitude, double longitude,
                         LocalTime openHour, LocalTime closeHour, List<MultipartFile> imageFiles,
                         Long ownerId, String provinceId, String districtId, String wardId
                         ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude; 
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.imageFiles = imageFiles;
        this.status = ParkingLotStatus.PENDING; // Set default status to PENDING
        this.ownerId = ownerId;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        // Fetch names for province, district, and ward
        fetchLocationNames();
    }

    public void fetchLocationNames() {
        this.provinceName = LocationService.getProvinceName(this.provinceId);
        this.districtName = LocationService.getDistrictName(this.provinceId, this.districtId);
        this.wardName = LocationService.getWardName(this.districtId, this.wardId);
        
    }
}
