package com.project.parkingfinder.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.model.ParkingLot;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    @Query(value = "SELECT * FROM parking_lots " +
           "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) <= :radius " +
           "AND status = 'ACTIVE'",
           nativeQuery = true)
    List<ParkingLot> findParkingLotsInRegion(@Param("latitude") Double latitude,
                                             @Param("longitude") Double longitude,
                                             @Param("radius") Double radius);

    @Query(value = "SELECT pl FROM ParkingLot pl WHERE pl.status = :status",
           countQuery = "SELECT COUNT(pl) FROM ParkingLot pl WHERE pl.status = :status")
    List<ParkingLot> findByStatus(@Param("status") ParkingLotStatus status, Pageable pageable);
}
