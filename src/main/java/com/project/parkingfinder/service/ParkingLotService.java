package com.project.parkingfinder.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.dto.ParkingLotProjection;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.Media;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.repository.MediaRepository;
import com.project.parkingfinder.repository.ParkingLotRepository;

@Service
public class ParkingLotService  {

    @Autowired
    private ParkingLotRepository parkingLotRepository;


    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public ParkingLotDTO createParkingLot(ParkingLotDTO parkingLotDTO) {
        // Tạo đối tượng ParkingLot từ DTO
        ParkingLot parkingLot = convertToEntity(parkingLotDTO);
        // Bước 1: Lưu ParkingLot trước để có được ID
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);
        
        // In ra saved ID
        System.out.println("Saved ParkingLot ID: " + savedParkingLot.getId());
        // Bước 2: Xử lý file và tạo danh sách Media
        List<Media> mediaList = new ArrayList<>();
        if (parkingLotDTO.getImageFiles() != null && !parkingLotDTO.getImageFiles().isEmpty()) {
            for (MultipartFile file : parkingLotDTO.getImageFiles()) {
                try {
                    // Lưu file và tạo Media
                    String imageUrl = fileStorageService.storeFile(file);
                    Media media = new Media();
                    media.setTableId(savedParkingLot.getId()); // Gán id của ParkingLot
                    media.setTableType(Media.TableType.PARKING_LOT);
                    media.setMediaType(Media.MediaType.IMAGE);
                    media.setUrl(imageUrl);
                    mediaList.add(media);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store file", e);
                }
            }
        }

        // Bước 3: Lưu Media vào cơ sở dữ liệu
        if (!mediaList.isEmpty()) {
            mediaRepository.saveAll(mediaList);
        }

        // Bước 4: Trả về DTO của ParkingLot đã tạo
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
        List<ParkingLotProjection> parkingLotsData = parkingLotRepository.findParkingLotsInRegionWithTotalSlots(latitude, longitude, radius);
        
        Map<Long, ParkingLotDTO> dtoMap = new HashMap<>();

        for (ParkingLotProjection projection : parkingLotsData) {
            dtoMap.computeIfAbsent(projection.getId(), id -> createDTO(projection));
            if (projection.getImageUrl() != null) {
                dtoMap.get(projection.getId()).getImages().add(projection.getImageUrl());
            }
        }

        return new ArrayList<>(dtoMap.values());
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
//TODO: make polymorphism for media
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
     private ParkingLotDTO createDTO(ParkingLotProjection projection) {
        ParkingLotDTO dto = new ParkingLotDTO();
        dto.setId(projection.getId());
        dto.setOwnerId(projection.getOwnerId());
        dto.setProvinceId(projection.getProvinceId());
        dto.setDistrictId(projection.getDistrictId());
        dto.setWardId(projection.getWardId());
        dto.setName(projection.getName());
        dto.setAddress(projection.getAddress());
        dto.setLongitude(projection.getLongitude());
        dto.setLatitude(projection.getLatitude());
        dto.setOpenHour(projection.getOpenHour());
        dto.setCloseHour(projection.getCloseHour());
        dto.setStatus(projection.getStatus());
        dto.setCapacity(projection.getTotalParkingSlots().intValue());
        dto.setImages(new ArrayList<>());
        dto.fetchLocationNames();
        return dto;
    }

}
