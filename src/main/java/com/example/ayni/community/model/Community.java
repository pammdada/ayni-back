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
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    @Column(name = "cover_url")
    private String coverUrl;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Community() {
    }

    public Community(String name, String description, String locationName, Double latitude, Double longitude, UUID createdBy) {
        this(name, description, locationName, latitude, longitude, createdBy, Visibility.PUBLIC, null);
    }

    public Community(String name, String description, String locationName, Double latitude, Double longitude,
                     UUID createdBy, Visibility visibility, String coverUrl) {
        id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        this.visibility = visibility;
        this.coverUrl = coverUrl;
    }

    @PrePersist
    void created() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
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

    public Visibility getVisibility() {
        return visibility;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public enum Visibility {PUBLIC, PRIVATE}
}
