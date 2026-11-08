package com.example.ayni.follow.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProfileFollowId implements Serializable {
    private UUID followerId;
    private UUID followedId;

    public ProfileFollowId() {
    }

    public ProfileFollowId(UUID followerId, UUID followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public UUID getFollowedId() {
        return followedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileFollowId that)) return false;
        return Objects.equals(followerId, that.followerId) && Objects.equals(followedId, that.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }
}
