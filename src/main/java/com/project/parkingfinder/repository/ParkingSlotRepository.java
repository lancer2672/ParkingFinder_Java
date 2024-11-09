package com.project.parkingfinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.model.ParkingSlot;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    
    List<ParkingSlot> findByParkingLotId(Long parkingLotId);

    Optional<ParkingSlot> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleTypeEnum vehicleType);

    Optional<ParkingSlot> findById(Long parkingLotId);
}
