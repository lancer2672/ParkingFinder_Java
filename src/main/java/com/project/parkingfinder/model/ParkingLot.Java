package com.project.parkingfinder.model;

import java.time.LocalTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.project.parkingfinder.enums.ParkingLotStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "parking_lots")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "ward_id")
    private Long wardId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "open_hour")
    private LocalTime openHour;

    @Column(name = "close_hour")
    private LocalTime closeHour;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParkingLotStatus status;
    
    @OneToMany(mappedBy = "tableId", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("table_type = 'PARKING_LOT'")
    private List<Media> media;

    public ParkingLot() {}

    public boolean isApproved() {
        return status == ParkingLotStatus.ACTIVE;
    }
}
