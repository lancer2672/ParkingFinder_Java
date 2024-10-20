package com.project.parkingfinder.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            @Valid @RequestParam("latitude") Double latitude,
            @Valid @RequestParam("openHour") String openHour,
            @Valid @RequestParam("longitude") Double longitude,
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

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsByMerchant(
            @PathVariable("merchantId") Long merchantId,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsByMerchant(merchantId, limit, offset);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ParkingLotDTO> updateParkingLot(
            @PathVariable("id") Long id,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(value = "address", required = false) String address,
            @Valid @RequestParam(value = "latitude", required = false) Double latitude,
            @Valid @RequestParam(value = "longitude", required = false) Double longitude,
            @Valid @RequestParam(value = "openHour", required = false) String openHour,
            @Valid @RequestParam(value = "closeHour", required = false) String closeHour,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @Valid @RequestParam(value = "ownerId", required = false) Long ownerId,
            @Valid @RequestParam(value = "provinceId", required = false) String provinceId,
            @Valid @RequestParam(value = "districtId", required = false) String districtId,
            @Valid @RequestParam(value = "wardId", required = false) String wardId,
            @Valid @RequestParam(value = "status", required = false) ParkingLotStatus status) {
        
        ParkingLotDTO parkingLotDTO = new ParkingLotDTO();
        parkingLotDTO.setId(id);
        parkingLotDTO.setName(name);
        parkingLotDTO.setAddress(address);
        parkingLotDTO.setLatitude(latitude);
        parkingLotDTO.setLongitude(longitude);
        parkingLotDTO.setOpenHour(openHour != null ? LocalTime.parse(openHour) : null);
        parkingLotDTO.setCloseHour(closeHour != null ? LocalTime.parse(closeHour) : null);
        parkingLotDTO.setImageFiles(imageFiles);
        parkingLotDTO.setOwnerId(ownerId);
        parkingLotDTO.setProvinceId(provinceId);
        parkingLotDTO.setDistrictId(districtId);
        parkingLotDTO.setWardId(wardId);
        parkingLotDTO.setStatus(status);
        
        ParkingLotDTO updatedParkingLot = parkingLotService.updateParkingLot(parkingLotDTO);
        return new ResponseEntity<>(updatedParkingLot, HttpStatus.OK);
    }
}
