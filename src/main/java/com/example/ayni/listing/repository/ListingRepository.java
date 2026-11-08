package com.example.ayni.listing.repository;

import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.listing.model.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.*;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findByStatusNot(Listing.Status status);

    List<Listing> findByOwnerIdAndStatusNot(UUID owner, Listing.Status status);

    @Query("""
            select l from Listing l
            where l.status = :status
              and l.createdAt < :before
              and (
                l.ownerId = :user
                or l.ownerId in (select f.followedId from ProfileFollow f where f.followerId = :user)
                or l.communityId in (select m.communityId from CommunityMember m
                    where m.userId = :user and m.status = :activeMember)
              )
            order by l.createdAt desc
            """)
    List<Listing> findFeedFor(@Param("user") UUID user,
                              @Param("before") Instant before,
                              @Param("status") Listing.Status status,
                              @Param("activeMember") CommunityMember.Status activeMember,
                              Pageable pageable);
}
