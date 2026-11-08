package com.example.ayni.post.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_likes")
@IdClass(PostLikeId.class)
public class PostLike {
    @Id
    @Column(name = "post_id")
    private UUID postId;
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "created_at")
    private Instant createdAt;

    protected PostLike() {
    }

    public PostLike(UUID postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
    }

    @PrePersist
    void created() {
        createdAt = Instant.now();
    }

    public UUID getPostId() {
        return postId;
    }

    public UUID getUserId() {
        return userId;
    }
}
