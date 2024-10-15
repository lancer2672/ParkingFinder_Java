package com.project.parkingfinder.model;

import javax.persistence.*;
import java.time.LocalTime;


@Entity
@Table(name = "ParkingLots")
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

    @Column(name = "street_address")
    private String streetAddress;

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

    public ParkingLot() {}

    public boolean isApproved() {
        return status == ParkingLotStatus.ACTIVE;
    }
}