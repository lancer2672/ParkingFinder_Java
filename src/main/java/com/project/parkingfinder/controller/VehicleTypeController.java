package com.project.parkingfinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.model.VehicleType;
import com.project.parkingfinder.service.VehicleTypeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/vehicle-types")
public class VehicleTypeController {
    
    private final VehicleTypeService vehicleTypeService;

    @Autowired
    public VehicleTypeController(VehicleTypeService vehicleTypeService) {
        this.vehicleTypeService = vehicleTypeService;
    }   

        @PostMapping
        public ResponseEntity<VehicleType> createVehicleType(@Valid @RequestBody CreateVehicleTypeRequest request) {
            VehicleType createdVehicleType = vehicleTypeService.createVehicleType(request.toVehicleType());
            return new ResponseEntity<>(createdVehicleType, HttpStatus.CREATED);
        }

        public static class CreateVehicleTypeRequest {
            @JsonProperty("parkingLotId")
            @NotNull(message = "ID bãi đỗ xe là bắt buộc")
            private Long parkingLotId;
            @JsonProperty("type")
            @NotNull(message = "Loại xe là bắt buộc")
            private VehicleTypeEnum type;

            @NotNull(message = "Giá là bắt buộc")
            @JsonProperty("price")
            @Positive(message = "Giá phải là số dương")
            private Double price;

            public VehicleType toVehicleType() {

                VehicleType vehicleType = new VehicleType();
                vehicleType.setParkingLotId(this.parkingLotId);
                vehicleType.setType(this.type);
                vehicleType.setPrice(this.price);
                return vehicleType;
            }
        }
}
