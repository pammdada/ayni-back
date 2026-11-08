package com.example.ayni.help.repository;

import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.help.model.HelpPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.*;

public interface HelpPostRepository extends JpaRepository<HelpPost, UUID> {
    List<HelpPost> findByStatus(HelpPost.Status status);

    @Query("""
            select h from HelpPost h
            where h.status = :status
              and h.createdAt < :before
              and (
                h.userId = :user
                or h.userId in (select f.followedId from ProfileFollow f where f.followerId = :user)
                or h.communityId in (select m.communityId from CommunityMember m
                    where m.userId = :user and m.status = :activeMember)
              )
            order by h.createdAt desc
            """)
    List<HelpPost> findFeedFor(@Param("user") UUID user,
                               @Param("before") Instant before,
                               @Param("status") HelpPost.Status status,
                               @Param("activeMember") CommunityMember.Status activeMember,
                               Pageable pageable);
}
