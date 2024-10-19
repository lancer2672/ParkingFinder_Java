package com.project.parkingfinder.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.Media;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.repository.ParkingLotRepository;

@Service
public class ParkingLotService  {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public ParkingLotDTO createParkingLot(ParkingLotDTO parkingLotDTO) {
        List<String> imageUrls = new ArrayList<>();
        if (parkingLotDTO.getImageFiles() != null) {
            for (MultipartFile file : parkingLotDTO.getImageFiles()) {
                try {
                    String imageUrl = fileStorageService.storeFile(file);
                    imageUrls.add(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store file", e);
                }
            }
        }
        parkingLotDTO.setImages(imageUrls);

        ParkingLot parkingLot = convertToEntity(parkingLotDTO);
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);
        return convertToDTO(savedParkingLot);
    }

    public ParkingLotDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
            .orElseThrow(() -> {
                throw new ResourceNotFoundException("ParkingLot not found with id: " + id);
            });
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

    public List<ParkingLotDTO> getParkingLotsByStatus(ParkingLotStatus status, int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        List<ParkingLot> parkingLots = parkingLotRepository.findByStatus(status, pageRequest);
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
            parkingLot.setLatitude(dto.getLatitude());
            parkingLot.setLongitude(dto.getLongitude());
            parkingLot.setStatus(dto.getStatus());
            parkingLot.setOwnerId(dto.getOwnerId());
            parkingLot.setProvinceId(dto.getProvinceId());
            parkingLot.setDistrictId(dto.getDistrictId());
            parkingLot.setWardId(dto.getWardId());
            parkingLot.setOpenHour(dto.getOpenHour());
            parkingLot.setCloseHour(dto.getCloseHour());
            
            // Update media if new image files are provided
            if (dto.getImageFiles() != null && !dto.getImageFiles().isEmpty()) {
                List<Media> newMedia = dto.getImageFiles().stream()
                    .map(file -> {
                        try {
                            Media media = new Media();
                            media.setTableId(parkingLot.getId());
                            media.setTableType(Media.TableType.PARKING_LOT);
                            media.setMediaType(Media.MediaType.IMAGE);
                            media.setUrl(fileStorageService.storeFile(file));
                            return media;
                        } catch (IOException e) {

                            // You might want to throw a custom exception here
                            throw new RuntimeException("Error storing file", e);
                        }
                    })
                    .collect(Collectors.toList());
                
                if (parkingLot.getMedia() == null) {
                    parkingLot.setMedia(new ArrayList<>());
                }
                parkingLot.getMedia().addAll(newMedia);
            }
        }

    private ParkingLotDTO convertToDTO(ParkingLot parkingLot) {
        List<String> imageUrls = parkingLot.getMedia() != null
            ? parkingLot.getMedia().stream()
                .filter(media -> Media.MediaType.IMAGE == media.getMediaType())
                .map(Media::getUrl)
                .collect(Collectors.toList())
            : Collections.emptyList();
        ParkingLotDTO dto = new ParkingLotDTO();
        dto.setId(parkingLot.getId());
        dto.setName(parkingLot.getName());
        dto.setAddress(parkingLot.getAddress());
        dto.setCapacity(1); // Assuming getCapacity() exists
        dto.setLatitude(parkingLot.getLatitude());
        dto.setLongitude(parkingLot.getLongitude());
        dto.setStatus(parkingLot.getStatus());
        dto.setImages(imageUrls);
        dto.setOwnerId(parkingLot.getOwnerId());
        dto.setProvinceId(parkingLot.getProvinceId());
        dto.setDistrictId(parkingLot.getDistrictId());
        dto.setWardId(parkingLot.getWardId());
        dto.setOpenHour(parkingLot.getOpenHour());
        dto.setCloseHour(parkingLot.getCloseHour());
        dto.fetchLocationNames();
        return dto;
    }

}
