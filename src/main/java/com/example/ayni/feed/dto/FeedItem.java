package com.example.ayni.feed.dto;

import com.example.ayni.profile.dto.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record FeedItem(
        UUID id,
        String itemType,
        ProfileSummary author,
        UUID communityId,
        String title,
        String summary,
        String imageUrl,
        Instant createdAt,
        long likeCount,
        long commentCount
) {
}
