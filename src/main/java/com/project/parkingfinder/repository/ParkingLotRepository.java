package com.project.parkingfinder.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.dto.ParkingLotProjection;
import com.project.parkingfinder.enums.ParkingLotStatus;
import com.project.parkingfinder.model.ParkingLot;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
       @Query(value = "SELECT pl.*, " +
              "COALESCE((SELECT SUM(ps.active_slots) FROM parking_slots ps WHERE ps.parking_lot_id = pl.id), 0) AS total_parking_slots, " +
              "m.url AS image_url " +
              "FROM parking_lots pl " +
              "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE' " +
              "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(pl.latitude)) * cos(radians(pl.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(pl.latitude)))) <= :radius " +
              "AND pl.status = 'ACTIVE'",
              nativeQuery = true)
       List<ParkingLotProjection> findParkingLotsInRegionWithTotalSlots(@Param("latitude") Double latitude,
                                                                        @Param("longitude") Double longitude,
                                                                        @Param("radius") Double radius);
    @Query(value = "SELECT pl.*, " +
           "COALESCE((SELECT SUM(ps.active_slots) FROM parking_slots ps WHERE ps.parking_lot_id = pl.id), 0) AS total_parking_slots, " +
           "m.url AS image_url " +
           "FROM parking_lots pl " +
           "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE' " +
           "WHERE pl.status = :status",
           nativeQuery = true)
    List<ParkingLotProjection> findByStatusWithTotalSlots(@Param("status") String status, Pageable pageable);


}


