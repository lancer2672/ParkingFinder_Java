package com.project.parkingfinder.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkingfinder.dto.ReservationDTO;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.enums.ReservationStatus;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.model.ParkingSlot;
import com.project.parkingfinder.model.Reservation;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.model.VehicleType;
import com.project.parkingfinder.repository.ParkingLotRepository;
import com.project.parkingfinder.repository.ParkingSlotRepository;
import com.project.parkingfinder.repository.ReservationRepository;
import com.project.parkingfinder.repository.UserRepository;
import com.project.parkingfinder.repository.VehicleTypeRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        //TODO check overlap, check open close, check status, ... khách vãng lai
        User user = userRepository.findById(reservationDTO.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
    
        ParkingLot parkingLot = parkingLotRepository.findById(reservationDTO.getParkingLotId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bãi đỗ xe"));

        LocalTime startTime = reservationDTO.getStartTime().toLocalTime(); // Assuming this returns LocalDateTime
        LocalTime endTime = reservationDTO.getEndTime().toLocalTime(); // Assuming this returns LocalDateTime

        if (parkingLot.getOpenHour().isAfter(startTime)) {
                throw new IllegalArgumentException("Bãi đỗ chưa mở cửa vào thời gian này"); 
        }
        
        if (parkingLot.getCloseHour().isBefore(endTime)) {
            throw new IllegalArgumentException("Bãi đỗ đã đóng cửa vào thời gian này");
        }

        if (parkingLot.getStatus().equals(ParkingLotStatus.INACTIVE)) {
            throw new IllegalArgumentException("Bãi đỗ xe đã ngưng hoạt động");
        }
        if (parkingLot.getStatus().equals(ParkingLotStatus.PENDING)) {
            throw new IllegalArgumentException("Bãi đỗ xe chưa được hoạt động");
        }
        ParkingSlot parkingSlot = parkingSlotRepository.findById(reservationDTO.getParkingLotId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chỗ đỗ xe"));
                
        VehicleType vehicleType = vehicleTypeRepository.findByTypeAndParkingLotId(reservationDTO.getVehicleType(), parkingSlot.getParkingLotId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại xe cho bãi đỗ xe đã cho"));
        Long countReservation = reservationRepository.countReservationsInTimeRange(parkingSlot.getId(), reservationDTO.getStartTime(), reservationDTO.getEndTime())
            .orElseThrow(() -> new EntityNotFoundException("Lỗi khi đếm số lượng đặt chỗ"));
            
        if (countReservation > parkingSlot.getActiveSlots()) {
            throw new IllegalArgumentException("Không đủ chỗ đỗ xe trong khoảng thời gian đã chọn");
        }
        Reservation reservation = new Reservation();
        reservation.setParkingSlot(parkingSlot);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCarType(vehicleType.getType());
        reservation.setPrice(vehicleType.getPrice());
        reservation.setCheckInTime(reservationDTO.getStartTime());
        reservation.setCheckOutTime(reservationDTO.getEndTime());

        Reservation savedReservation = reservationRepository.save(reservation);

        return convertToDTO(savedReservation, vehicleType);
    }

    private ReservationDTO convertToDTO(Reservation reservation, VehicleType vehicleType) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setParkingLotId(reservation.getParkingSlot().getId());
        dto.setStatus(reservation.getStatus());
        dto.setStartTime(reservation.getCheckInTime());
        dto.setEndTime(reservation.getCheckOutTime());
        dto.setVehicleType(vehicleType.getType().toString());
        dto.setTotalPrice(reservation.getPrice());
        return dto;
    }
}