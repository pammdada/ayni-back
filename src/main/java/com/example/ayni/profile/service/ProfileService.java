package com.example.ayni.profile.service;

import com.example.ayni.profile.dto.*;
import com.example.ayni.profile.model.*;
import com.example.ayni.profile.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profiles;
    private final ProfileTagRepository tags;

    public ProfileService(ProfileRepository profiles, ProfileTagRepository tags) {
        this.profiles = profiles;
        this.tags = tags;
    }

    public ProfileResponse create(UUID userId, ProfileRequests.Create request) {
        if (profiles.existsById(userId)) throw new IllegalArgumentException("El perfil ya existe");
        return ProfileResponse.from(profiles.save(new Profile(userId, request.accountType(), request.fullName(), request.username(), request.email())));
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(UUID id) {
        return ProfileResponse.from(profile(id));
    }

    public ProfileResponse update(UUID id, ProfileRequests.Update request) {
        Profile p = profile(id);
        p.update(request.fullName(), request.phone(), request.bio(), request.avatarUrl());
        return ProfileResponse.from(p);
    }

    public ProfileResponse updateTags(UUID id, ProfileRequests.Tags request) {
        Profile p = profile(id);
        Set<ProfileTag> found = tags.findByNameIn(request.tags());
        if (found.size() != request.tags().size()) throw new IllegalArgumentException("Una o más etiquetas no existen");
        p.setTags(found);
        return ProfileResponse.from(p);
    }

    private Profile profile(UUID id) {
        return profiles.findById(id).orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));
    }
}
