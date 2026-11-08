package com.example.ayni.emergency.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_locations")
public class UserLocation {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    private Double latitude;
    private Double longitude;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Column(name = "sharing_enabled")
    private boolean sharingEnabled;

    protected UserLocation() {
    }

    public UserLocation(UUID id, Double lat, Double lon, boolean enabled) {
        userId = id;
        latitude = lat;
        longitude = lon;
        sharingEnabled = enabled;
        updatedAt = Instant.now();
    }

    public void update(Double lat, Double lon, boolean enabled) {
        latitude = lat;
        longitude = lon;
        sharingEnabled = enabled;
        updatedAt = Instant.now();
    }
}
