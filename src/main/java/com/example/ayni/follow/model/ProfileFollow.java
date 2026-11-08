package com.example.ayni.follow.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profile_follows")
@IdClass(ProfileFollowId.class)
public class ProfileFollow {
    @Id
    @Column(name = "follower_id")
    private UUID followerId;
    @Id
    @Column(name = "followed_id")
    private UUID followedId;
    @Column(name = "created_at")
    private Instant createdAt;

    protected ProfileFollow() {
    }

    public ProfileFollow(UUID followerId, UUID followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    @PrePersist
    void created() {
        createdAt = Instant.now();
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public UUID getFollowedId() {
        return followedId;
    }
}
