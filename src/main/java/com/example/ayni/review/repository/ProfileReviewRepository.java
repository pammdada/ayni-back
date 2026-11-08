package com.example.ayni.review.repository;

import com.example.ayni.review.model.ProfileReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileReviewRepository extends JpaRepository<ProfileReview, UUID> {
    List<ProfileReview> findByProfileIdOrderByCreatedAtDesc(UUID profileId);

    Optional<ProfileReview> findByProfileIdAndReviewerId(UUID profileId, UUID reviewerId);

    void deleteByProfileIdAndReviewerId(UUID profileId, UUID reviewerId);
}
