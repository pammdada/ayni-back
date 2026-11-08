package com.example.ayni.post.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    private UUID id;
    @Column(name = "author_id", nullable = false)
    private UUID authorId;
    @Column(name = "community_id")
    private UUID communityId;
    @Enumerated(EnumType.STRING)
    private Kind kind;
    @Column(length = 160)
    private String title;
    @Column(nullable = false, length = 4000)
    private String content;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Post() {
    }

    public Post(UUID authorId, UUID communityId, Kind kind, String title, String content, Visibility visibility) {
        id = UUID.randomUUID();
        this.authorId = authorId;
        this.communityId = communityId;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
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

    public void update(Kind kind, String title, String content, Visibility visibility) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
    }

    public void delete() {
        status = Status.DELETED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getCommunityId() {
        return communityId;
    }

    public Kind getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public enum Kind {POST, ANNOUNCEMENT}

    public enum Visibility {PUBLIC, FOLLOWERS, COMMUNITY}

    public enum Status {ACTIVE, HIDDEN, DELETED}
}
