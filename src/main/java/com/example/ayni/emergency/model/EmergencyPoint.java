package com.example.ayni.emergency.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "emergency_points")
public class EmergencyPoint {
    @Id
    private UUID id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String status;

    protected EmergencyPoint() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }
}
