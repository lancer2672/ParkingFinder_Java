package com.project.parkingfinder.dto;

import java.time.LocalTime;

import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.model.User;
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

    @NotNull(message = "ID chủ sở hữu là bắt buộc")
    private Long ownerId;
    @NotNull(message = "ID tỉnh là bắt buộc")
    private String provinceId;
    private String provinceName;

    @NotNull(message = "ID quận là bắt buộc")
    private String districtId;
    private String districtName;

    @NotNull(message = "ID phường là bắt buộc")
    private String wardId;
    private String wardName;

    @NotBlank(message = "Tên là bắt buộc")
    private String name;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    private String address;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer capacity;

    @NotNull(message = "Vĩ độ là bắt buộc")
    private Double latitude;

    @NotNull(message = "Kinh độ là bắt buộc")
    private Double longitude;

    @NotNull(message = "Giờ mở cửa là bắt buộc")
    private LocalTime openHour;

    @NotNull(message = "Giờ đóng cửa là bắt buộc")
    private LocalTime closeHour;


    private ParkingLotStatus status;

//    @JsonIgnore
    private String imageUrls;

    private User owner;
    
    // Constructor for creating a new ParkingLotDTO with image files
    public ParkingLotDTO(String name, String address, double latitude, double longitude,
                         LocalTime openHour, LocalTime closeHour, String imageUrls,
                         Long ownerId, String provinceId, String districtId, String wardId
                         ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude; 
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.imageUrls = imageUrls;
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

    public void addUser(User user) {
        this.owner = user;
    }
}
