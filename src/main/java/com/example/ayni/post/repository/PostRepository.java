package com.example.ayni.post.repository;

import com.example.ayni.community.model.Community;
import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.post.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByAuthorIdAndStatusNotOrderByCreatedAtDesc(UUID authorId, Post.Status status);

    List<Post> findByCommunityIdAndStatusNotOrderByCreatedAtDesc(UUID communityId, Post.Status status);

    @Query("""
            select p from Post p
            where p.status = :active
              and p.createdAt < :before
              and (
                p.authorId = :user
                or (p.visibility = :publicVisibility and (p.communityId is null
                    or p.communityId in (select c.id from Community c where c.visibility = :publicCommunity)))
                or (p.visibility = :followersVisibility and p.communityId is null
                    and p.authorId in (select f.followedId from ProfileFollow f where f.followerId = :user))
                or (p.communityId in (select m.communityId from CommunityMember m
                    where m.userId = :user and m.status = :activeMember))
              )
            order by p.createdAt desc
            """)
    List<Post> findFeedFor(@Param("user") UUID user,
                           @Param("before") Instant before,
                           @Param("active") Post.Status active,
                           @Param("publicVisibility") Post.Visibility publicVisibility,
                           @Param("followersVisibility") Post.Visibility followersVisibility,
                           @Param("publicCommunity") Community.Visibility publicCommunity,
                           @Param("activeMember") CommunityMember.Status activeMember,
                           Pageable pageable);
}
