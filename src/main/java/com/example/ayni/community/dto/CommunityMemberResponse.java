package com.example.ayni.community.dto;

import com.example.ayni.community.model.CommunityMember;

import java.time.Instant;
import java.util.UUID;

public record CommunityMemberResponse(UUID userId, String role, String status, Instant joinedAt) {
    public static CommunityMemberResponse from(CommunityMember member) {
        return new CommunityMemberResponse(member.getUserId(), member.getRole().name(), member.getStatus().name(),
                member.getJoinedAt());
    }
}
