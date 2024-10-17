package com.project.parkingfinder.service;

import com.project.parkingfinder.dto.ParkingLotDTO;

public interface ParkingLotService {
    ParkingLotDTO createParkingLot(ParkingLotDTO parkingLotDTO);
    ParkingLotDTO getParkingLotById(Long id);
    ParkingLotDTO updateParkingLot(Long id, ParkingLotDTO parkingLotDTO);
    void deleteParkingLot(Long id);
    // You can add more methods as needed, such as:
    // List<ParkingLotDTO> getAllParkingLots();
    // List<ParkingLotDTO> getParkingLotsByStatus(ParkingLotStatus status);
}
