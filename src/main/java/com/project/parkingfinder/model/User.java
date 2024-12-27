package com.project.parkingfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.parkingfinder.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parking_lot_id")
    @JsonIgnore
    private ParkingLot parkingLot;

    @Column(name = "parking_lot_id", insertable = false, updatable = false)
    private Long parkingLotId;

    private String name;
    private String phoneNumber;

    @JsonIgnore
    private String password;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id")
    private User merchant;

    private String avatar; // Added avatar field

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", role=" + (role != null ? role.getName() : "null") +
                ", status=" + status +
                ", merchantId=" + (merchant != null ? merchant.getId() : "null") +
                ", avatar='" + avatar + '\'' + // Added avatar to toString
                '}';
    }
}

