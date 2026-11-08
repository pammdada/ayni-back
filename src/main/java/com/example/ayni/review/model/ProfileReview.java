package com.example.ayni.review.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profile_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uq_profile_review_reviewer", columnNames = {"profile_id", "reviewer_id"})
})
public class ProfileReview {
    @Id
    private UUID id;
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;
    @Column(nullable = false)
    private String task;
    @Column(nullable = false)
    private int stars;
    private String comment;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected ProfileReview() {
    }

    public ProfileReview(UUID profileId, UUID reviewerId, String task, int stars, String comment) {
        this.id = UUID.randomUUID();
        this.profileId = profileId;
        this.reviewerId = reviewerId;
        this.task = task;
        this.stars = stars;
        this.comment = comment;
    }

    @PrePersist
    void created() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public String getTask() {
        return task;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(String task, int stars, String comment) {
        this.task = task;
        this.stars = stars;
        this.comment = comment;
    }
}
