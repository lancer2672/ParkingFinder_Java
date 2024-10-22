    package com.project.parkingfinder.repository;

    import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.VehicleType;

    @Repository
    public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
        
        @Query(value = "SELECT * FROM vehicle_types WHERE type = :type AND parking_lot_id = :parkingLotId", nativeQuery = true)
        Optional<VehicleType> findByTypeAndParkingLotId(@Param("type") String type, @Param("parkingLotId") Long parkingLotId);
    }   
