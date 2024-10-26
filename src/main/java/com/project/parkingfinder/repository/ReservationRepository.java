package com.project.parkingfinder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

   @Query(value = "SELECT COUNT(*) FROM reservations r WHERE " +
                   "(r.check_in_time BETWEEN :checkIn AND :checkOut OR " +
                   "r.check_out_time BETWEEN :checkIn AND :checkOut) " +
                   "AND r.parking_slot_id = :parkingSlotId AND r.status != 'CANCELLED'", nativeQuery = true)
    Optional<Long> countReservationsInTimeRange(@Param("parkingSlotId") Long parkingSlotId,
                                                @Param("checkIn") LocalDateTime checkIn,
                                                @Param("checkOut") LocalDateTime checkOut);
}
