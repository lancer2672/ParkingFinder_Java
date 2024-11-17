package com.project.parkingfinder.service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.PreDestroy;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.quartz.*;
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

    private final Scheduler scheduler;

    @Autowired
    public ReservationService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PreDestroy
    public void destroy() throws SchedulerException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
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
        Long freeSlots = parkingSlot.getActiveSlots() - countReservation;
        if (freeSlots <= 0) {
            throw new IllegalArgumentException("Không còn chỗ đỗ xe trống trong khoảng thời gian đã chọn");
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
        try {
        this.scheduleTicketCancellation(savedReservation.getId());
        }catch (Exception e) {
            throw new InternalError("Failed to create reservation: " + e.getMessage(), e);
        }
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

    public void updateReservation(Long reservationId, ReservationStatus status) {
        try{

            Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dữ liệu"));
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
        catch (Exception e) {
            throw new InternalError("Lỗi khi cập nhật trạng thái đặt chỗ");
        }
        
    }

    public UserReservationsResponse getUserReservations(Long userId, int page, int size) {
        try{

            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Reservation> reservationsPage = reservationRepository.findByUserId(userId, pageRequest);
            List<Reservation> reservations = reservationsPage.getContent();
            long totalRecords = reservationsPage.getTotalElements();
            List<ReservationDTO> dtos = new ArrayList<>();
            for (Reservation reservation : reservations) {
                VehicleType vehicleType = vehicleTypeRepository.findByTypeAndParkingLotId(reservation.getCarType().toString(), reservation.getParkingSlot().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại xe cho bãi đỗ xe đã cho"));
                dtos.add(convertToDTO(reservation, vehicleType));
            }
            UserReservationsResponse response = new UserReservationsResponse(dtos, totalRecords);
            return response;
        }
        catch (Exception e) {
            throw new InternalError("Lỗi khi lấy dữ liệu đặt chỗ của người dùng");
        }
        
    }



    public void scheduleTicketCancellation(Long ticketId) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(CancelTicketJob.class)
                .withIdentity("cancelTicketJob-" + ticketId)
                .usingJobData("ticketId", ticketId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("cancelTicketTrigger-" + ticketId)
                .startAt(Date.from(Instant.now().plus(30, ChronoUnit.SECONDS)))
                .build();
        System.out.println("Trigger created for ticket ID: " + ticketId);
        scheduler.scheduleJob(job, trigger);
    }



    public static class UserReservationsResponse {
        private final List<ReservationDTO> reservations;
        private final long totalRecords;

        // Constructor, getters, and setters
        public UserReservationsResponse(List<ReservationDTO> reservations, long totalRecords) {
            this.reservations = reservations;
            this.totalRecords = totalRecords;
        }

        public List<ReservationDTO> getReservations() {
            return reservations;
        }

        public long getTotalRecords() {
            return totalRecords;
        }
    }

}

