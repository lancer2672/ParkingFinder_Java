package com.project.parkingfinder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.repository.ParkingLotRepository;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    public ParkingLotDTO createParkingLot(ParkingLotDTO parkingLotDTO) {
        ParkingLot parkingLot = convertToEntity(parkingLotDTO);
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);
        return convertToDTO(savedParkingLot);
    }

    public ParkingLotDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found with id: " + id));
        return convertToDTO(parkingLot);
    }

    public ParkingLotDTO updateParkingLot(Long id, ParkingLotDTO parkingLotDTO) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found with id: " + id));
        
        updateParkingLotFromDTO(parkingLot, parkingLotDTO);
        ParkingLot updatedParkingLot = parkingLotRepository.save(parkingLot);
        return convertToDTO(updatedParkingLot);
    }

    public void deleteParkingLot(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new ResourceNotFoundException("ParkingLot not found with id: " + id);
        }
        parkingLotRepository.deleteById(id);
    }

    public List<ParkingLotDTO> getAllParkingLots(int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        return parkingLotRepository.findAll(pageRequest).getContent()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ParkingLotDTO> getParkingLotsInRegion(Double latitude, Double longitude, Double radius) {
        List<ParkingLot> parkingLots = parkingLotRepository.findParkingLotsInRegion(latitude, longitude, radius);
        return parkingLots.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private ParkingLot convertToEntity(ParkingLotDTO dto) {
        ParkingLot parkingLot = new ParkingLot();
        updateParkingLotFromDTO(parkingLot, dto);
        return parkingLot;
    }

    private void updateParkingLotFromDTO(ParkingLot parkingLot, ParkingLotDTO dto) {
        parkingLot.setName(dto.getName());
        parkingLot.setAddress(dto.getAddress());
        parkingLot.setCapacity(dto.getCapacity());
        parkingLot.setLatitude(dto.getLatitude());
        parkingLot.setLongitude(dto.getLongitude());
        parkingLot.setStatus(dto.getStatus());
    }

    private ParkingLotDTO convertToDTO(ParkingLot parkingLot) {
        return new ParkingLotDTO(
            parkingLot.getId(),
            parkingLot.getName(),
            parkingLot.getAddress(),
            parkingLot.getCapacity(),
            parkingLot.getLatitude(),
            parkingLot.getLongitude(),
            parkingLot.getStatus()
        );
    }
}
