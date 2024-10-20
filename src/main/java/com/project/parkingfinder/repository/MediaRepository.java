package com.project.parkingfinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkingfinder.model.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
}
