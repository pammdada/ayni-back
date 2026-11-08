package com.example.ayni.community.repository;

import com.example.ayni.community.model.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, UUID> {
    Optional<CommunityMember> findByCommunityIdAndUserId(UUID communityId, UUID userId);

    List<CommunityMember> findByCommunityId(UUID communityId);

    List<CommunityMember> findByCommunityIdAndStatus(UUID communityId, CommunityMember.Status status);

    List<CommunityMember> findByUserIdAndStatus(UUID userId, CommunityMember.Status status);

    boolean existsByCommunityIdAndUserIdAndStatus(UUID communityId, UUID userId, CommunityMember.Status status);
}
