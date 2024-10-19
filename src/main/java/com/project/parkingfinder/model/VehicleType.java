package com.project.parkingfinder.model;

import com.project.parkingfinder.enums.VehicleTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "vehicle_types")
@Getter
@Setter
public class VehicleType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "parking_lot_id", nullable = false)
    private Long parkingLotId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleTypeEnum type;
    
    @Column(name = "price", nullable = false)
    private Double price;

}
