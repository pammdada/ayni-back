package com.example.ayni.profile.dto;

import com.example.ayni.profile.model.Profile;

import java.util.UUID;

public record ProfileSummary(UUID id, String username, String fullName, String avatarUrl) {
    public static ProfileSummary from(Profile p) {
        return new ProfileSummary(p.getId(), p.getUsername(), p.getFullName(), p.getAvatarUrl());
    }
}
