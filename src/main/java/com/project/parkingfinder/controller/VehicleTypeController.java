package com.project.parkingfinder.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @NotNull(message = "Parking lot ID is required")
            private Long parkingLotId;
            @JsonProperty("type")
            @NotNull(message = "Vehicle type is required")
            private VehicleTypeEnum type;

            @NotNull(message = "Price is required")
            @JsonProperty("price")
            @Positive(message = "Price must be positive")
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
