package com.project.parkingfinder.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.dto.ParkingLotDetail;
import com.project.parkingfinder.dto.ParkingSlotDTO;
import com.project.parkingfinder.dto.VehicleDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.enums.VehicleTypeEnum;
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

    @PostMapping
    public ResponseEntity<ParkingLotDTO> createParkingLot(@Valid @RequestBody ParkingLotDTO parkingLotDTO) {
        ParkingLotDTO createdParkingLot = parkingLotService.createParkingLot(parkingLotDTO);
        return new ResponseEntity<>(createdParkingLot, HttpStatus.CREATED);
    }

    @GetMapping("region")
    public ResponseEntity<List<ParkingLotDTO>> getActiveParkingLotsInRegion(
            @RequestParam(name = "lat") Double latitude,
            @RequestParam(name = "lng") Double longitude,
            @RequestParam(name = "type") VehicleTypeEnum type,
            @RequestParam(name = "radius_km") Double radius) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsInRegion(latitude, longitude, radius,type);
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
        @NotNull(message = "ID loại xe là bắt buộc")
        private Long vehicleTypeId;

        @NotNull(message = "Số lượng là bắt buộc")
        @Positive(message = "Số lượng phải là số dương")
        private Integer quantity;
    }

    @GetMapping("/status")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsByStatus(
            @RequestParam(name = "status") ParkingLotStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsByStatus(status, page, size);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }

    @GetMapping("/free-slot")
    public ResponseEntity<Map<String, Long>> countFreeSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam(name = "parkingLotId") Long parkinglotId,
            @RequestParam(name = "type") VehicleTypeEnum type    

            ) {
        Long count = parkingLotService.countFreeSlots(parkinglotId,type,checkIn);
        Map<String, Long> response = new HashMap<>();
        response.put("free_slot", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{parkingLotId}/vehicles")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByParkingLot(
        @PathVariable("parkingLotId") Long parkingLotId) {
        List<VehicleDTO> result = parkingLotService.getVehiclesAndSlots(parkingLotId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsByMerchant(
            @PathVariable("merchantId") Long merchantId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsByMerchant(merchantId, page, size);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLots(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getAllParkingLots(page, size);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLotDetail> getParkingLotById(@PathVariable("id") Long id) {
        ParkingLotDetail parkingLot = parkingLotService.getParkingLotDetail(id);
        return new ResponseEntity<>(parkingLot, HttpStatus.OK);
    }
    
    @PatchMapping(value = "/{id}")
    public ResponseEntity<ParkingLotDTO> updateParkingLot(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParkingLotDTO parkingLotDTO) {
        
        parkingLotDTO.setId(id);
        
        if (parkingLotDTO.getOpenHour() != null) {
            parkingLotDTO.setOpenHour(LocalTime.parse(parkingLotDTO.getOpenHour().toString()));
        }
        
        if (parkingLotDTO.getCloseHour() != null) {
            parkingLotDTO.setCloseHour(LocalTime.parse(parkingLotDTO.getCloseHour().toString())); 
        }
        
        ParkingLotDTO updatedParkingLot = parkingLotService.updateParkingLot(parkingLotDTO);
        return new ResponseEntity<>(updatedParkingLot, HttpStatus.OK);
    }
}
