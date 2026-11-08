package com.example.ayni.review.dto;

import com.example.ayni.profile.dto.ProfileSummary;
import com.example.ayni.review.model.ProfileReview;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID profileId,
        ProfileSummary reviewer,
        String task,
        int stars,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
    public static ReviewResponse from(ProfileReview review, ProfileSummary reviewer) {
        return new ReviewResponse(
                review.getId(),
                review.getProfileId(),
                reviewer,
                review.getTask(),
                review.getStars(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
