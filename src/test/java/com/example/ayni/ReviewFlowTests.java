package com.example.ayni;

import com.example.ayni.profile.model.AccountType;
import com.example.ayni.profile.model.Profile;
import com.example.ayni.profile.repository.ProfileRepository;
import com.example.ayni.review.dto.ReviewRequests;
import com.example.ayni.review.dto.ReviewResponse;
import com.example.ayni.review.dto.ReviewSummary;
import com.example.ayni.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReviewFlowTests {
    @Autowired
    private ProfileRepository profiles;
    @Autowired
    private ReviewService reviews;

    private UUID owner;
    private UUID reviewer;

    @BeforeEach
    void setUp() {
        owner = profile("owner");
        reviewer = profile("reviewer");
    }

    @Test
    void reviewsStartEmptyAndSummarizeRatings() {
        assertThat(reviews.list(owner)).isEmpty();

        ReviewSummary empty = reviews.summary(owner, reviewer);
        assertThat(empty.count()).isZero();
        assertThat(empty.average()).isZero();
        assertThat(empty.myReview()).isNull();

        ReviewResponse created = reviews.upsert(owner, reviewer,
                new ReviewRequests.Upsert(5, "Gasfiteria", "Excelente trabajo"));

        assertThat(created.reviewer()).isNotNull();
        assertThat(created.reviewer().id()).isEqualTo(reviewer);

        ReviewSummary summary = reviews.summary(owner, reviewer);
        assertThat(summary.count()).isEqualTo(1);
        assertThat(summary.average()).isEqualTo(5.0);
        assertThat(summary.myReview()).isNotNull();
        assertThat(summary.byTask()).hasSize(1);
        assertThat(summary.byTask().get(0).task()).isEqualTo("Gasfiteria");
        assertThat(summary.byTask().get(0).average()).isEqualTo(5.0);
    }

    @Test
    void oneReviewPerReviewerIsUpdated() {
        reviews.upsert(owner, reviewer, new ReviewRequests.Upsert(3, "Pintura", "Regular"));
        reviews.upsert(owner, reviewer, new ReviewRequests.Upsert(4, "Pintura", "Mejor"));

        List<ReviewResponse> found = reviews.list(owner);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).stars()).isEqualTo(4);
        assertThat(found.get(0).comment()).isEqualTo("Mejor");
    }

    @Test
    void cannotReviewOwnProfile() {
        assertThatThrownBy(() -> reviews.upsert(owner, owner,
                new ReviewRequests.Upsert(5, "Gasfiteria", "Yo mismo")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reviewCanBeDeleted() {
        reviews.upsert(owner, reviewer, new ReviewRequests.Upsert(5, "Clases", "Bueno"));
        reviews.delete(owner, reviewer);

        assertThat(reviews.list(owner)).isEmpty();
        assertThat(reviews.summary(owner, reviewer).count()).isZero();
    }

    private UUID profile(String prefix) {
        UUID id = UUID.randomUUID();
        String suffix = id.toString().substring(0, 8);
        profiles.saveAndFlush(new Profile(id, AccountType.INDIVIDUAL, prefix + " " + suffix,
                prefix + "_" + suffix, prefix + "_" + suffix + "@example.com"));
        return id;
    }
}
