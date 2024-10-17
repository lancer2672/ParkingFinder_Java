package com.project.parkingfinder.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.project.parkingfinder.service.ParkingLotService;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    public ResponseEntity<ParkingLotDTO> createParkingLot(@RequestBody ParkingLotDTO parkingLotDTO) {
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

    @GetMapping("/region")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsInRegion(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius) {
        List<ParkingLotDTO> parkingLots = parkingLotService.getParkingLotsInRegion(latitude, longitude, radius);
        return new ResponseEntity<>(parkingLots, HttpStatus.OK);
    }

    @PostMapping("/{parkingLotId}/slots")
    public ResponseEntity<ParkingSlotDTO> addParkingSlot(
            @PathVariable Long parkingLotId,
            @RequestBody ParkingSlotDTO parkingSlotDTO) {
        ParkingSlotDTO createdParkingSlot = parkingLotService.addParkingSlot(parkingLotId, parkingSlotDTO);
        return new ResponseEntity<>(createdParkingSlot, HttpStatus.CREATED);
    }

    @GetMapping("/{parkingLotId}/slots")
    public ResponseEntity<List<ParkingSlotDTO>> getParkingSlotsByParkingLotId(@PathVariable Long parkingLotId) {
        List<ParkingSlotDTO> parkingSlots = parkingLotService.getParkingSlotsByParkingLotId(parkingLotId);
        return new ResponseEntity<>(parkingSlots, HttpStatus.OK);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ParkingLotDTO> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            ParkingLotDTO updatedParkingLot = parkingLotService.uploadImage(id, file);
            return new ResponseEntity<>(updatedParkingLot, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
