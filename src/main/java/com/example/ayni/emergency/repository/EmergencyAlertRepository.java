package com.example.ayni.emergency.repository;

import com.example.ayni.emergency.model.EmergencyAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, UUID> {
}
