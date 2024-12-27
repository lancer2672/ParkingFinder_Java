package com.project.parkingfinder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT COUNT(*) FROM reservations r " +
            "WHERE r.start_time <= :currentTime " +
            "AND r.parking_slot_id = :parkingSlotId " +
            "AND r.status = 'CHECKED_IN' OR r.status = 'PENDING'", nativeQuery = true)
    Optional<Long> countCheckedInReservations(@Param("parkingSlotId") Long parkingSlotId,
                                              @Param("currentTime") LocalDateTime currentTime);


    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId ORDER BY r.startTime DESC")
    Page<Reservation> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
