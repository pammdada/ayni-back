package com.example.ayni.community.dto;

import com.example.ayni.community.model.Community;
import jakarta.validation.constraints.*;

public final class CommunityRequests {
    private CommunityRequests() {
    }

    public record Create(@NotBlank @Size(max = 120) String name, @Size(max = 1000) String description,
                         @Size(max = 255) String locationName, Double latitude, Double longitude,
                         Community.Visibility visibility, @Size(max = 2048) String coverUrl) {
    }

    public record Membership(@NotNull Boolean approved) {
    }
}
