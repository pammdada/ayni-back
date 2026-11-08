package com.example.ayni.follow.repository;

import com.example.ayni.follow.model.ProfileFollow;
import com.example.ayni.follow.model.ProfileFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProfileFollowRepository extends JpaRepository<ProfileFollow, ProfileFollowId> {
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    List<ProfileFollow> findByFollowedId(UUID followedId);

    @Query("select f.followedId from ProfileFollow f where f.followerId = :follower")
    List<UUID> findFollowedIds(@Param("follower") UUID follower);

    @Query("select f.followerId from ProfileFollow f where f.followedId = :followed")
    List<UUID> findFollowerIds(@Param("followed") UUID followed);
}
