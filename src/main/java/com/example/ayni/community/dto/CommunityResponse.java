package com.example.ayni.community.dto;

import com.example.ayni.community.model.Community;

import java.time.Instant;
import java.util.UUID;

public record CommunityResponse(UUID id, String name, String description, String locationName, UUID createdBy,
                                String visibility, String coverUrl, Instant createdAt, long memberCount) {
    public static CommunityResponse from(Community c, long memberCount) {
        return new CommunityResponse(c.getId(), c.getName(), c.getDescription(), c.getLocationName(), c.getCreatedBy(),
                c.getVisibility().name(), c.getCoverUrl(), c.getCreatedAt(), memberCount);
    }
}
