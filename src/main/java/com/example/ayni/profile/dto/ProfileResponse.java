package com.example.ayni.profile.dto;
import com.example.ayni.profile.model.Profile; import java.util.Set; import java.util.UUID;
public record ProfileResponse(UUID id, String accountType, String fullName, String username, String email, String phone, String bio, String avatarUrl, Set<String> tags) { public static ProfileResponse from(Profile p){return new ProfileResponse(p.getId(),p.getAccountType().name(),p.getFullName(),p.getUsername(),p.getEmail(),p.getPhone(),p.getBio(),p.getAvatarUrl(),p.getTags().stream().map(t->t.getName()).collect(java.util.stream.Collectors.toSet()));} }
