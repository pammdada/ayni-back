package com.example.ayni.review.service;

import com.example.ayni.profile.dto.ProfileSummary;
import com.example.ayni.profile.model.Profile;
import com.example.ayni.profile.repository.ProfileRepository;
import com.example.ayni.review.dto.ReviewRequests;
import com.example.ayni.review.dto.ReviewResponse;
import com.example.ayni.review.dto.ReviewSummary;
import com.example.ayni.review.model.ProfileReview;
import com.example.ayni.review.repository.ProfileReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    private final ProfileReviewRepository reviews;
    private final ProfileRepository profiles;

    public ReviewService(ProfileReviewRepository reviews, ProfileRepository profiles) {
        this.reviews = reviews;
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> list(UUID profileId) {
        requireProfile(profileId);
        List<ProfileReview> found = reviews.findByProfileIdOrderByCreatedAtDesc(profileId);
        Map<UUID, ProfileSummary> reviewers = reviewerProfiles(found);
        return found.stream()
                .map(review -> ReviewResponse.from(review, reviewers.get(review.getReviewerId())))
                .toList();
    }

    public ReviewResponse upsert(UUID profileId, UUID reviewerId, ReviewRequests.Upsert request) {
        requireProfile(profileId);
        if (profileId.equals(reviewerId)) {
            throw new IllegalArgumentException("No puede calificar su propio perfil");
        }
        ProfileReview saved = reviews.findByProfileIdAndReviewerId(profileId, reviewerId)
                .map(existing -> {
                    existing.update(request.task(), request.stars(), request.comment());
                    return existing;
                })
                .orElseGet(() -> reviews.save(new ProfileReview(profileId, reviewerId, request.task(),
                        request.stars(), request.comment())));
        return ReviewResponse.from(saved, profileSummary(reviewerId));
    }

    public void delete(UUID profileId, UUID reviewerId) {
        requireProfile(profileId);
        reviews.findByProfileIdAndReviewerId(profileId, reviewerId).ifPresent(reviews::delete);
    }

    @Transactional(readOnly = true)
    public ReviewSummary summary(UUID profileId, UUID viewerId) {
        requireProfile(profileId);
        List<ProfileReview> found = reviews.findByProfileIdOrderByCreatedAtDesc(profileId);
        double average = round(found.stream().mapToInt(ProfileReview::getStars).average().orElse(0));
        Map<String, List<ProfileReview>> grouped = found.stream()
                .collect(Collectors.groupingBy(ProfileReview::getTask));
        List<ReviewSummary.TaskRating> byTask = grouped.entrySet().stream()
                .map(entry -> new ReviewSummary.TaskRating(
                        entry.getKey(),
                        round(entry.getValue().stream().mapToInt(ProfileReview::getStars).average().orElse(0)),
                        entry.getValue().size()))
                .sorted(Comparator.comparingLong(ReviewSummary.TaskRating::count).reversed())
                .toList();
        ReviewResponse myReview = viewerId == null ? null : found.stream()
                .filter(review -> review.getReviewerId().equals(viewerId))
                .findFirst()
                .map(review -> ReviewResponse.from(review, profileSummary(viewerId)))
                .orElse(null);
        return new ReviewSummary(average, found.size(), byTask, myReview);
    }

    private Map<UUID, ProfileSummary> reviewerProfiles(List<ProfileReview> found) {
        if (found.isEmpty()) return Map.of();
        Set<UUID> ids = found.stream().map(ProfileReview::getReviewerId).collect(Collectors.toSet());
        return profiles.findAllById(ids).stream()
                .collect(Collectors.toMap(Profile::getId, ProfileSummary::from));
    }

    private ProfileSummary profileSummary(UUID id) {
        return profiles.findById(id).map(ProfileSummary::from).orElse(null);
    }

    private void requireProfile(UUID id) {
        if (!profiles.existsById(id)) {
            throw new EntityNotFoundException("Perfil no encontrado");
        }
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
