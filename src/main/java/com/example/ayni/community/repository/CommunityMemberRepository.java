package com.example.ayni.community.repository;

import com.example.ayni.community.model.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, UUID> {
    Optional<CommunityMember> findByCommunityIdAndUserId(UUID communityId, UUID userId);

    List<CommunityMember> findByCommunityId(UUID communityId);

    List<CommunityMember> findByCommunityIdAndStatus(UUID communityId, CommunityMember.Status status);

    List<CommunityMember> findByUserIdAndStatus(UUID userId, CommunityMember.Status status);

    boolean existsByCommunityIdAndUserIdAndStatus(UUID communityId, UUID userId, CommunityMember.Status status);

    long countByCommunityIdAndStatus(UUID communityId, CommunityMember.Status status);

    @Query("""
            select m.communityId, count(m) from CommunityMember m
            where m.communityId in :communityIds and m.status = :status
            group by m.communityId
            """)
    List<Object[]> countByCommunityIds(@Param("communityIds") Collection<UUID> communityIds,
                                       @Param("status") CommunityMember.Status status);

    @Query("""
            select m.communityId from CommunityMember m
            where m.userId = :userId and m.status = :status
            """)
    List<UUID> findCommunityIdsByUserIdAndStatus(@Param("userId") UUID userId,
                                                 @Param("status") CommunityMember.Status status);
}
