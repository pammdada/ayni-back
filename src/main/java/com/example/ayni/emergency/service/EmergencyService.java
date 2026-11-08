package com.example.ayni.emergency.service;

import com.example.ayni.emergency.dto.LocationRequest;
import com.example.ayni.emergency.model.*;
import com.example.ayni.emergency.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class EmergencyService {
    private final EmergencyAlertRepository alerts;
    private final EmergencyPointRepository points;
    private final UserLocationRepository locations;

    public EmergencyService(EmergencyAlertRepository a, EmergencyPointRepository p, UserLocationRepository l) {
        alerts = a;
        points = p;
        locations = l;
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlert> alerts() {
        return alerts.findAll();
    }

    @Transactional(readOnly = true)
    public List<EmergencyPoint> points() {
        return points.findAll();
    }

    public void update(UUID user, LocationRequest r) {
        UserLocation l = locations.findById(user).orElse(new UserLocation(user, r.latitude(), r.longitude(), r.sharingEnabled()));
        l.update(r.latitude(), r.longitude(), r.sharingEnabled());
        locations.save(l);
    }
}
