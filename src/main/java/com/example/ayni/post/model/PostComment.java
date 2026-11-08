package com.example.ayni.post.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    private UUID id;
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    @Column(name = "author_id", nullable = false)
    private UUID authorId;
    @Column(name = "parent_id")
    private UUID parentId;
    @Column(nullable = false, length = 2000)
    private String content;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected PostComment() {
    }

    public PostComment(UUID postId, UUID authorId, UUID parentId, String content) {
        id = UUID.randomUUID();
        this.postId = postId;
        this.authorId = authorId;
        this.parentId = parentId;
        this.content = content;
        status = Status.ACTIVE;
    }

    @PrePersist
    void created() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
    }

    public void delete() {
        status = Status.DELETED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPostId() {
        return postId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public String getContent() {
        return content;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public enum Status {ACTIVE, DELETED}
}
