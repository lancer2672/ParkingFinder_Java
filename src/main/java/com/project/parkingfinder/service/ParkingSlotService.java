package com.project.parkingfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.ParkingSlotDTO;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.ParkingSlot;
import com.project.parkingfinder.model.VehicleType;
import com.project.parkingfinder.repository.ParkingLotRepository;
import com.project.parkingfinder.repository.ParkingSlotRepository;
import com.project.parkingfinder.repository.VehicleTypeRepository;

@Service
public class ParkingSlotService {
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    public ParkingSlotService(ParkingSlotRepository parkingSlotRepository, ParkingLotRepository parkingLotRepository, VehicleTypeRepository vehicleTypeRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public ParkingSlotDTO addParkingSlots(Long parkingLotId, Long vehicleTypeId, Integer quantity) {

        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found with id: " + vehicleTypeId));

        parkingLotRepository.findById(parkingLotId)
            .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + parkingLotId));

        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setParkingLotId(parkingLotId);
        parkingSlot.setVehicleType(vehicleType.getType());
        parkingSlot.setTotalSlots(quantity);
        parkingSlot.setActiveSlots(quantity);

        ParkingSlot savedParkingSlot = parkingSlotRepository.save(parkingSlot);
        
        return convertToDTO(savedParkingSlot, vehicleType.getPrice());
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
