package com.example.ayni.profile.dto;

public record ProfileStatsResponse(
        long postCount,
        long followerCount,
        long followingCount,
        boolean followedByMe
) {
}
