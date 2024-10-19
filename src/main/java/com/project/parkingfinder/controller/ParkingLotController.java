package com.project.parkingfinder.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.dto.ParkingSlotDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.service.ParkingLotService;
import com.project.parkingfinder.service.ParkingSlotService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final ParkingSlotService parkingSlotService;


    public ParkingLotController(ParkingLotService parkingLotService, ParkingSlotService parkingSlotService) {
        this.parkingLotService = parkingLotService;
        this.parkingSlotService = parkingSlotService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ParkingLotDTO> createParkingLot(
            @Valid @RequestParam("name") String name,
            @Valid @RequestParam("address") String address,
            @Valid @RequestParam("latitude") double latitude,
            @Valid @RequestParam("openHour") String openHour,
            @Valid @RequestParam("longitude") double longitude,
            @Valid @RequestParam("closeHour") String closeHour,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @Valid @RequestParam("ownerId") Long ownerId,
            @Valid @RequestParam("provinceId") String provinceId,
            @Valid @RequestParam("districtId") String districtId,
            @Valid @RequestParam("wardId") String wardId) {
        
        ParkingLotDTO parkingLotDTO = new ParkingLotDTO(name, address, latitude, longitude,
                LocalTime.parse(openHour), LocalTime.parse(closeHour), imageFiles,
                ownerId, provinceId, districtId, wardId);
        
        ParkingLotDTO createdParkingLot = parkingLotService.createParkingLot(parkingLotDTO);
        return new ResponseEntity<>(createdParkingLot, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLots(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getAllParkingLots(limit, offset);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }

   
    @GetMapping("region")
    public ResponseEntity<List<ParkingLotDTO>> getActiveParkingLotsInRegion(
            @RequestParam(name = "lat") Double latitude,
            @RequestParam(name = "lng") Double longitude,
            @RequestParam(name = "radius_km") Double radius) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsInRegion(latitude, longitude, radius);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }
    @PostMapping("/{parkingLotId}/parking-slots")
    public ResponseEntity<ParkingSlotDTO> addParkingSlots(
            @PathVariable("parkingLotId") Long parkingLotId,
            @Valid @RequestBody ParkingSlotRequest parkingSlotRequest) {
        
        ParkingSlotDTO addedParkingSlot = parkingSlotService.addParkingSlots(parkingLotId, parkingSlotRequest.getVehicleTypeId(), parkingSlotRequest.getQuantity());
        return ResponseEntity.ok().body(addedParkingSlot);
    }


    @Setter
    @Getter
    public static class ParkingSlotRequest {
        @NotNull(message = "Vehicle type ID is required")
        private Long vehicleTypeId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }

    @GetMapping("/status")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsByStatus(
            @RequestParam(name = "status") ParkingLotStatus status,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsByStatus(status, limit, offset);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }
}
