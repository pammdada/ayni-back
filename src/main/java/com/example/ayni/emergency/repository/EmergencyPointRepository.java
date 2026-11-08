package com.example.ayni.emergency.repository;

import com.example.ayni.emergency.model.EmergencyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmergencyPointRepository extends JpaRepository<EmergencyPoint, UUID> {
}
