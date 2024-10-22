package com.project.parkingfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkingfinder.dto.ReservationDTO;
import com.project.parkingfinder.enums.ReservationStatus;
import com.project.parkingfinder.model.ParkingSlot;
import com.project.parkingfinder.model.Reservation;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.model.VehicleType;
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

    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        //TODO check overlap, check open close, check status, ... khách vãng lai
        User user = userRepository.findById(reservationDTO.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ParkingSlot parkingSlot = parkingSlotRepository.findById(reservationDTO.getParkingLotId())
            .orElseThrow(() -> new EntityNotFoundException("Parking slot not found"));

        VehicleType vehicleType = vehicleTypeRepository.findByTypeAndParkingLotId(reservationDTO.getVehicleType(), parkingSlot.getParkingLotId())
            .orElseThrow(() -> new EntityNotFoundException("Vehicle type not found for the given parking lot"));

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