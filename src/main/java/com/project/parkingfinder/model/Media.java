package com.project.parkingfinder.model;

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

@Getter
@Setter
@Entity
@Table(name = "medias")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "table_type", nullable = false)
    private TableType tableType;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "url", nullable = false)
    private String url;

    public Media() {}

    // Enum for media types
    public enum MediaType {
        IMAGE, VIDEO, AUDIO, DOCUMENT
    }

    // Enum for table types
    public enum TableType {
        PARKING_LOT, USER, BOOKING, REVIEW
    }
}