package com.project.parkingfinder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "parking_lot_id", nullable = false)
    private Long parkingLotId;
    
    @Column(name = "type_id", nullable = false)
    private Long typeId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    // Constructors, getters, and setters
    
    public enum Status {
        AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
    }
}
