package com.project.parkingfinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkingfinder.model.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Media m WHERE m.tableId = :tableId AND m.tableType = :tableType")
    void deleteByTableIdAndTableType(Long tableId, String tableType);
}
