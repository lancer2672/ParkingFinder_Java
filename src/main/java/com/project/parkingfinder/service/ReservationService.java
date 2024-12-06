package com.project.parkingfinder.service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.project.parkingfinder.model.*;
import com.project.parkingfinder.repository.*;
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
    private PaymentRepository paymentRepository;

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

        if (parkingLot.getOpenHour().isAfter(startTime)) {
                throw new IllegalArgumentException("Bãi đỗ chưa mở cửa vào thời gian này"); 
        }
        
//        if (parkingLot.getCloseHour().isBefore(endTime)) {
//            throw new IllegalArgumentException("Bãi đỗ đã đóng cửa vào thời gian này");
//        }

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
        Long countReservation = reservationRepository.countCheckedInReservations(parkingSlot.getId(), reservationDTO.getStartTime())
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
        try {
            // Tạo PageRequest để phân trang
            PageRequest pageRequest = PageRequest.of(page, 1000);
            Page<Reservation> reservationsPage = reservationRepository.findByUserId(userId, pageRequest);

            // Lấy danh sách đặt chỗ và tổng số bản ghi
            List<Reservation> reservations = reservationsPage.getContent();
            long totalRecords = reservationsPage.getTotalElements();

            // Tạo danh sách DTO
            List<ReservationDTO> dtos = new ArrayList<>();
            for (Reservation reservation : reservations) {
                // Tìm VehicleType
                VehicleType vehicleType = vehicleTypeRepository
                        .findByTypeAndParkingLotId(reservation.getCarType().toString(), reservation.getParkingSlot().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại xe cho bãi đỗ xe đã cho"));

                // Tìm Payment (nếu có)
                Optional<Payment> paymentOpt = paymentRepository.findPaymentByReservationId(reservation.getId());

                // Chuyển đổi Reservation sang DTO
                ReservationDTO dto = convertToDTO(reservation, vehicleType);

                Optional<ParkingLot> pkl = parkingLotRepository.findById(reservation.getParkingSlot().getParkingLotId());
                // Nếu Payment tồn tại, gán vào DTO
                paymentOpt.ifPresent(dto::setPayment);
                pkl.ifPresent(dto::setParkingLot);

                // Thêm DTO vào danh sách
                dtos.add(dto);
            }

            // Tạo và trả về response
            return new UserReservationsResponse(dtos, totalRecords);
        } catch (Exception e) {
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
                .startAt(Date.from(Instant.now().plus(90, ChronoUnit.SECONDS)))
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

