package com.example.ayni.post.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_media")
public class PostMedia {
    @Id
    private UUID id;
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    @Column(nullable = false, length = 2048)
    private String url;
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    private MediaType mediaType;
    @Column(name = "`position`")
    private int position;
    @Column(name = "created_at")
    private Instant createdAt;

    protected PostMedia() {
    }

    public PostMedia(UUID postId, String url, MediaType mediaType, int position) {
        id = UUID.randomUUID();
        this.postId = postId;
        this.url = url;
        this.mediaType = mediaType;
        this.position = position;
    }

    @PrePersist
    void created() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPostId() {
        return postId;
    }

    public String getUrl() {
        return url;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public int getPosition() {
        return position;
    }

    public enum MediaType {IMAGE, VIDEO}
}
