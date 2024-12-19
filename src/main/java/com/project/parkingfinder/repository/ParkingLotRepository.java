package com.project.parkingfinder.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.dto.ParkingLotDetail;
import com.project.parkingfinder.dto.ParkingLotProjection;
import com.project.parkingfinder.dto.VehicleDTO;
import com.project.parkingfinder.model.ParkingLot;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    @Query(value = "SELECT pl.*, " +
            "COALESCE(SUM(ps.active_slots), 0) AS total_parking_slots, " +
            "m.url AS image_url " +
            "FROM parking_lots pl " +
            "LEFT JOIN parking_slots ps ON ps.parking_lot_id = pl.id AND ps.vehicle_type = :type " +
            "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE' " +
            "WHERE pl.status = 'ACTIVE' " +
            "AND (6371 * acos( " +
            "cos(radians(:latitude)) * cos(radians(pl.latitude)) * " +
            "cos(radians(pl.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(pl.latitude)) " +
            ")) <= :radius " +
            "GROUP BY pl.id, m.url " +
            "HAVING COALESCE(SUM(ps.active_slots), 0) > 0",
            nativeQuery = true)
    List<ParkingLotProjection> findParkingLotsInRegionWithTotalSlots(@Param("latitude") Double latitude,
                                                                     @Param("longitude") Double longitude,
                                                                     @Param("radius") Double radius,
                                                                     @Param("type") String type);

    @Query(value = "SELECT pl.*, " +
           "COALESCE((SELECT SUM(ps.active_slots) FROM parking_slots ps WHERE ps.parking_lot_id = pl.id), 0) AS total_parking_slots, " +
           "m.url AS image_url " +
           "FROM parking_lots pl " +
           "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE' " +
           "WHERE pl.status = :status",
           nativeQuery = true)
    List<ParkingLotProjection> findByStatusWithTotalSlots(@Param("status") String status, Pageable pageable);

    @Query(value = "SELECT pl.*, " +
           "COALESCE((SELECT SUM(ps.active_slots) FROM parking_slots ps WHERE ps.parking_lot_id = pl.id), 0) AS total_parking_slots, " +
           "m.url AS image_url " +
           "FROM parking_lots pl " +
           "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE' " +
           "WHERE pl.owner_id = :ownerId",
           nativeQuery = true)
    List<ParkingLotProjection> findByOwnerIdWithTotalSlots(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("""
    SELECT NEW com.project.parkingfinder.dto.VehicleDTO(
        vt.parkingLotId,
        vt.price,
        vt.type,
        ps.id,
        ps.totalSlots,
        ps.activeSlots
    )
       FROM VehicleType vt
       LEFT JOIN ParkingLot pl ON pl.id = vt.parkingLotId
       LEFT JOIN ParkingSlot ps ON ps.vehicleType = vt.type
       WHERE pl.id = :parkingLotId
       """)
    List<VehicleDTO> findVehiclesAndSlots(@Param("parkingLotId") Long parkingLotId);

    @Query(value = "SELECT pl.*, " +
           "COALESCE((SELECT SUM(ps.active_slots) FROM parking_slots ps WHERE ps.parking_lot_id = pl.id), 0) AS total_parking_slots, " +
           "m.url AS image_url " +
           "FROM parking_lots pl " +
           "LEFT JOIN medias m ON m.table_id = pl.id AND m.table_type = 'PARKING_LOT' AND m.media_type = 'IMAGE'",
           nativeQuery = true)
    List<ParkingLotProjection> findAllWithTotalSlots(Pageable pageable);

  @Query("""
    SELECT NEW com.project.parkingfinder.dto.ParkingLotDetail(
        pl.id,
        pl.ownerId,
        pl.provinceId,
        pl.districtId,
        pl.wardId,
        pl.name,
        pl.address,
        pl.latitude,
        pl.longitude,
        pl.openHour,
        pl.closeHour,
        pl.status,
        pl.imageUrls,
        u
        )
    FROM ParkingLot pl
    LEFT JOIN User u ON u.id = pl.ownerId
    WHERE pl.id = :id
    """)
ParkingLotDetail findParkingLotDetail(@Param("id") Long id);

@Query("""
    SELECT NEW com.project.parkingfinder.dto.VehicleDTO(
        vt.parkingLotId,
        vt.price,
        vt.type,
        ps.id,
        ps.totalSlots,
        ps.activeSlots)
    FROM VehicleType vt
    LEFT JOIN ParkingSlot ps ON ps.parkingLotId = vt.parkingLotId AND ps.vehicleType = vt.type
    WHERE vt.parkingLotId = :parkingLotId
    """)
List<VehicleDTO> findVehiclesByParkingLotId(@Param("parkingLotId") Long parkingLotId);


       @Query("""
       SELECT COALESCE(SUM(ps.totalSlots), 0)
       FROM ParkingSlot ps
       WHERE ps.parkingLotId = :id
       """)
       Integer findTotalSlotsByParkingLotId(@Param("id") Long id);


}
