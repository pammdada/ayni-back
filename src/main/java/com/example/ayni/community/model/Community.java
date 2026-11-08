package com.example.ayni.community.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "communities")
public class Community {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(name = "location_name")
    private String locationName;
    private Double latitude;
    private Double longitude;
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    @Column(name = "created_at")
    private Instant createdAt;

    protected Community() {
    }

    public Community(String name, String description, String locationName, Double latitude, Double longitude, UUID createdBy) {
        id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
    }

    @PrePersist
    void created() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }
}
