package com.project.parkingfinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.enums.VehicleTypeEnum;
import com.project.parkingfinder.model.ParkingSlot;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

List<ParkingSlot> findByParkingLotId(Long parkingLotId);

List<ParkingSlot> findByParkingLotIdAndVehicleType(Long parkingLotId, VehicleTypeEnum vehicleType);

ParkingSlot findByParkingLotIdAndId(Long parkingLotId, Long id);
}
