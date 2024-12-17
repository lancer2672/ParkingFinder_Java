package com.project.parkingfinder.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.dto.ParkingLotDTO;
import com.project.parkingfinder.dto.ParkingLotProjection;
import com.project.parkingfinder.dto.VehicleDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.Media;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.model.ParkingSlot;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.repository.MediaRepository;
import com.project.parkingfinder.repository.ParkingLotRepository;
import com.project.parkingfinder.repository.ParkingSlotRepository;
import com.project.parkingfinder.repository.ReservationRepository;
import com.project.parkingfinder.repository.UserRepository;

@Service
public class ParkingLotService  {

    @Autowired
    private ParkingLotRepository parkingLotRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserRepository userRepo;


    @Transactional
    public ParkingLotDTO createParkingLot(ParkingLotDTO parkingLotDTO) {
        ParkingLot parkingLot = convertToEntity(parkingLotDTO);
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);
        
        System.out.println("Saved ParkingLot ID: " + savedParkingLot.getId());
        
        List<Media> mediaList = new ArrayList<>();
        if (parkingLotDTO.getImageFiles() != null && !parkingLotDTO.getImageFiles().isEmpty()) {
            for (MultipartFile file : parkingLotDTO.getImageFiles()) {
                try {
                    String imageUrl = fileStorageService.storeFile(file);
                    Media media = new Media();
                    media.setTableId(savedParkingLot.getId());
                    media.setTableType(Media.TableType.PARKING_LOT);
                    media.setMediaType(Media.MediaType.IMAGE);
                    media.setUrl(imageUrl);
                    mediaList.add(media);
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi khi lưu file", e);
                }
            }
        }

        if (!mediaList.isEmpty()) {
            mediaRepository.saveAll(mediaList);
        }
        savedParkingLot.setMedia(mediaList);
        return convertToDTO(savedParkingLot,0);
    }

    public Long countFreeSlots(Long parkingLotId, VehicleTypeEnum type, LocalDateTime checkIn) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByParkingLotIdAndVehicleType(parkingLotId,type)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chỗ đỗ xe"));
        Long count =  reservationRepository.countCheckedInReservations(parkingSlot.getId(), checkIn)
        .orElseThrow(() -> new ResourceNotFoundException("Lỗi khi đếm số lượng chỗ trống"));

        System.out.println(count);
        Long freeSlots = parkingSlot.getActiveSlots() - count;
        return freeSlots < 0 ? 0 : freeSlots;
    }
    public List<VehicleDTO> getVehiclesAndSlots(Long parkingLotId) {

        try{
            List<VehicleDTO> vehicles =  parkingLotRepository.findVehiclesAndSlots(parkingLotId);
            return vehicles;
        }catch( Exception e){
            System.out.println(e);
            throw new InternalException("Lỗi khi lấy thông tin bãi đỗ xe");
        }
    }
    public ParkingLotDTO getParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
            .orElseThrow(() -> {
                throw new ResourceNotFoundException("Không tìm thấy bãi đỗ xe với id: " + id);
            });
        return convertToDTO(parkingLot,null);
    }
    public ParkingLot getById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
            .orElseThrow(() -> {
                throw new ResourceNotFoundException("Không tìm thấy bãi đỗ xe với id: " + id);
            });
        return parkingLot;
    }

    @Transactional
    public ParkingLotDTO updateParkingLot(ParkingLotDTO parkingLotDTO) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bãi đỗ xe với id: " + parkingLotDTO.getId()));
        
        updateParkingLotFields(parkingLot, parkingLotDTO);
        updateParkingLotImages(parkingLot, parkingLotDTO.getImageFiles());
        
        ParkingLot updatedParkingLot = parkingLotRepository.save(parkingLot);
        return convertToDTO(updatedParkingLot,null);
    }

    private void updateParkingLotFields(ParkingLot parkingLot, ParkingLotDTO parkingLotDTO) {
        if (parkingLotDTO.getName() != null) parkingLot.setName(parkingLotDTO.getName());
        if (parkingLotDTO.getAddress() != null) parkingLot.setAddress(parkingLotDTO.getAddress());
        if (parkingLotDTO.getLatitude() != null) parkingLot.setLatitude(parkingLotDTO.getLatitude());
        if (parkingLotDTO.getLongitude() != null) parkingLot.setLongitude(parkingLotDTO.getLongitude());
        if (parkingLotDTO.getOpenHour() != null) parkingLot.setOpenHour(parkingLotDTO.getOpenHour());
        if (parkingLotDTO.getCloseHour() != null) parkingLot.setCloseHour(parkingLotDTO.getCloseHour());
        if (parkingLotDTO.getOwnerId() != null) parkingLot.setOwnerId(parkingLotDTO.getOwnerId());
        if (parkingLotDTO.getProvinceId() != null) parkingLot.setProvinceId(parkingLotDTO.getProvinceId());
        if (parkingLotDTO.getDistrictId() != null) parkingLot.setDistrictId(parkingLotDTO.getDistrictId());
        if (parkingLotDTO.getWardId() != null) parkingLot.setWardId(parkingLotDTO.getWardId());
        if (parkingLotDTO.getStatus() != null) parkingLot.setStatus(parkingLotDTO.getStatus());
    }

    private void updateParkingLotImages(ParkingLot parkingLot, List<MultipartFile> imageFiles) {
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Media> newMediaList = createMediaList(parkingLot.getId(), imageFiles);
            mediaRepository.deleteByTableIdAndTableType(parkingLot.getId(), Media.TableType.PARKING_LOT.toString());
            mediaRepository.saveAll(newMediaList);
            parkingLot.setMedia(newMediaList);
        }
    }

    private List<Media> createMediaList(Long parkingLotId, List<MultipartFile> imageFiles) {
        return imageFiles.stream()
            .map(file -> createMedia(parkingLotId, file))
            .collect(Collectors.toList());
    }

    private Media createMedia(Long parkingLotId, MultipartFile file) {
        try {
            String imageUrl = fileStorageService.storeFile(file);
            Media media = new Media();
            media.setTableId(parkingLotId);
            media.setTableType(Media.TableType.PARKING_LOT);
            media.setMediaType(Media.MediaType.IMAGE);
            media.setUrl(imageUrl);
            return media;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file", e);
        }
    }

    public void deleteParkingLot(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bãi đỗ xe với id: " + id);
        }
        parkingLotRepository.deleteById(id);
    }


    public List<ParkingLotDTO> getParkingLotsInRegion(Double latitude, Double longitude, Double radius, VehicleTypeEnum type) {
        System.out.println("Fetching parking lots in region with latitude: " + latitude + ", longitude: " + longitude + ", radius: " + radius + ", and vehicle type: " + type);
        List<ParkingLotProjection> parkingLotsData = parkingLotRepository.findParkingLotsInRegionWithTotalSlots(latitude, longitude, radius, type.toString());
        Map<Long, ParkingLotDTO> dtoMap = new HashMap<>();
 
        for (ParkingLotProjection projection : parkingLotsData) {
            dtoMap.computeIfAbsent(projection.getId(), id -> createDTO(projection));
            if (projection.getImageUrl() != null) {
                dtoMap.get(projection.getId()).getImages().add(projection.getImageUrl());
            }
        }

        return new ArrayList<>(dtoMap.values());
    }

    public List<ParkingLotDTO> getParkingLotsByStatus(ParkingLotStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ParkingLotProjection> parkingLotsData = parkingLotRepository.findByStatusWithTotalSlots(status.toString(), pageRequest);
        
        Map<Long, ParkingLotDTO> dtoMap = new HashMap<>();
 
        for (ParkingLotProjection projection : parkingLotsData) {
            dtoMap.computeIfAbsent(projection.getId(), id -> createDTO(projection));
            if (projection.getImageUrl() != null) {

                dtoMap.get(projection.getId()).getImages().add(   projection.getImageUrl());
            }
        }

        return new ArrayList<>(dtoMap.values());
    }
    public List<ParkingLotDTO> getParkingLotsByMerchant(Long merchantId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ParkingLotProjection> parkingLotsData = parkingLotRepository.findByOwnerIdWithTotalSlots(merchantId, pageRequest);
        
        Map<Long, ParkingLotDTO> dtoMap = new HashMap<>();
 
        for (ParkingLotProjection projection : parkingLotsData) {
            dtoMap.computeIfAbsent(projection.getId(), id -> createDTO(projection));
            if (projection.getImageUrl() != null) {
                dtoMap.get(projection.getId()).getImages().add( projection.getImageUrl());
            }
        }

        return new ArrayList<>(dtoMap.values());
    }

    public List<ParkingLotDTO> getAllParkingLots(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ParkingLotProjection> parkingLotsData = parkingLotRepository.findAllWithTotalSlots(pageRequest);
        
        Map<Long, ParkingLotDTO> dtoMap = new HashMap<>();
 
        for (ParkingLotProjection projection : parkingLotsData) {
            ParkingLotDTO dto = dtoMap.computeIfAbsent(projection.getId(), id -> createDTO(projection));
            if (projection.getImageUrl() != null) {
                dto.getImages().add(projection.getImageUrl());
            }
            
            // Get owner details
            User owner = userRepo.findById(projection.getOwnerId()).orElseThrow();
            dto.addUser(owner);
        }

        return new ArrayList<>(dtoMap.values());
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
    
    private ParkingLotDTO convertToDTO(ParkingLot parkingLot, Integer capacity) {
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
        dto.setCapacity(capacity);
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
//        dto.fetchLocationNames();
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
//        dto.fetchLocationNames();
        return dto;
    }

}