package com.project.parkingfinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.Reservation;

    @Repository
    public interface ReservationRepository extends JpaRepository<Reservation, Long> {
        Reservation save(Reservation reservation);
    }
