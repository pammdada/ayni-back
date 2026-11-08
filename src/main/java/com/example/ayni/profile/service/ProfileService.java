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
    private static final int MAX_USERNAME_LENGTH = 50;

    private final ProfileRepository profiles;
    private final ProfileTagRepository tags;

    public ProfileService(ProfileRepository profiles, ProfileTagRepository tags) {
        this.profiles = profiles;
        this.tags = tags;
    }

    /**
     * Creación idempotente: si el perfil ya existe (o dos peticiones concurrentes
     * intentan crearlo) devuelve el perfil existente en lugar de fallar.
     */
    public ProfileResponse create(UUID userId, ProfileRequests.Create request) {
        Optional<Profile> existing = profiles.findWithTagsById(userId);
        if (existing.isPresent()) {
            return ProfileResponse.from(existing.get());
        }

        profiles.insertIfAbsent(
                userId,
                request.accountType().name(),
                request.fullName(),
                uniqueUsername(request.username()),
                request.email(),
                request.sector()
        );

        return profiles.findWithTagsById(userId)
                .map(ProfileResponse::from)
                .orElseThrow(() -> new IllegalStateException("No se pudo crear el perfil"));
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(UUID id) {
        return ProfileResponse.from(profileWithTags(id));
    }

    @Transactional(readOnly = true)
    public List<ProfileResponse> getMany(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return profiles.findAllWithTagsByIdIn(ids).stream()
                .map(ProfileResponse::from)
                .toList();
    }

    public ProfileResponse update(UUID id, ProfileRequests.Update request) {
        Profile p = profileWithTags(id);
        p.update(request.fullName(), request.sector(), request.phone(), request.bio(), request.avatarUrl());
        return ProfileResponse.from(p);
    }

    public ProfileResponse updateTags(UUID id, ProfileRequests.Tags request) {
        Profile p = profile(id);
        Set<String> requested = new HashSet<>(request.tags());
        Set<ProfileTag> found = tags.findByNameIn(requested);
        if (found.size() != requested.size()) {
            throw new IllegalArgumentException("Una o más etiquetas no existen");
        }
        p.setTags(found);
        return ProfileResponse.from(p);
    }

    @Transactional(readOnly = true)
    public List<ProfileTag> availableTags() {
        return tags.findAll();
    }

    @Transactional(readOnly = true)
    public ProfileStatsResponse stats(UUID id, UUID viewer) {
        List<Object[]> rows = profiles.stats(id, viewer);
        if (rows.isEmpty()) {
            throw new EntityNotFoundException("Perfil no encontrado");
        }
        Object[] row = rows.get(0);
        return new ProfileStatsResponse(
                number(row[0]), number(row[1]), number(row[2]), number(row[3]) > 0);
    }

    private long number(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private Profile profile(UUID id) {
        return profiles.findById(id).orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));
    }

    private Profile profileWithTags(UUID id) {
        return profiles.findWithTagsById(id).orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));
    }

    private String uniqueUsername(String desired) {
        String base = normalizeUsername(desired);
        if (!profiles.existsByUsername(base)) {
            return base;
        }
        for (int i = 1; i <= 99; i++) {
            String candidate = base + i;
            if (candidate.length() <= MAX_USERNAME_LENGTH && !profiles.existsByUsername(candidate)) {
                return candidate;
            }
        }
        String prefix = base.length() > MAX_USERNAME_LENGTH - 8
                ? base.substring(0, MAX_USERNAME_LENGTH - 8)
                : base;
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    private String normalizeUsername(String value) {
        String base = value == null ? "" : value.trim().toLowerCase().replaceAll("[^a-z0-9_]", "");
        if (base.isBlank()) {
            base = "usuario";
        }
        return base.length() > MAX_USERNAME_LENGTH ? base.substring(0, MAX_USERNAME_LENGTH) : base;
    }
}
