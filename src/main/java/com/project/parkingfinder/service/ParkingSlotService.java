package com.project.parkingfinder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.ParkingSlotDTO;
import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.ParkingSlot;
import com.project.parkingfinder.repository.ParkingLotRepository;
import com.project.parkingfinder.repository.ParkingSlotRepository;

@Service
public class ParkingSlotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;

    @Autowired
    public ParkingSlotService(ParkingSlotRepository parkingSlotRepository, ParkingLotRepository parkingLotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<ParkingSlotDTO> addParkingSlots(Long parkingLotId, VehicleTypeEnum vehicleType, Double price, Integer quantity) {
        parkingLotRepository.findById(parkingLotId)
            .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + parkingLotId));

        List<ParkingSlot> parkingSlotsToSave = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            ParkingSlot parkingSlot = new ParkingSlot();
            parkingSlot.setParkingLotId(parkingLotId);
            parkingSlot.setVehicleType(vehicleType);
            parkingSlot.setTotalSlots(quantity);
            parkingSlot.setActiveSlots(quantity);
            parkingSlotsToSave.add(parkingSlot);
        }

        List<ParkingSlot> savedParkingSlots = parkingSlotRepository.saveAll(parkingSlotsToSave);
        
        return savedParkingSlots.stream()
            .map(parkingSlot -> convertToDTO(parkingSlot, price))
            .collect(Collectors.toList());
    }

    private ParkingSlot convertToEntity(ParkingSlotDTO dto) {
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setParkingLotId(dto.getParkingLotId());
        parkingSlot.setVehicleType(dto.getType());
        parkingSlot.setTotalSlots(dto.getTotalSlots());
        parkingSlot.setActiveSlots(dto.getActiveSlots());
        return parkingSlot;
    }

    private ParkingSlotDTO convertToDTO(ParkingSlot parkingSlot, Double price) {
        ParkingSlotDTO dto = new ParkingSlotDTO();
        dto.setId(parkingSlot.getId());
        dto.setParkingLotId(parkingSlot.getParkingLotId());
        dto.setType(parkingSlot.getVehicleType());
        dto.setPrice(price);
        dto.setTotalSlots(parkingSlot.getTotalSlots());
        dto.setActiveSlots(parkingSlot.getActiveSlots());
        return dto;
    }
}
