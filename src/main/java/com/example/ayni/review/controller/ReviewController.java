package com.example.ayni.review.controller;

import com.example.ayni.review.dto.ReviewOverview;
import com.example.ayni.review.dto.ReviewRequests;
import com.example.ayni.review.dto.ReviewResponse;
import com.example.ayni.review.dto.ReviewSummary;
import com.example.ayni.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles/{profileId}/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReviewResponse> list(@PathVariable UUID profileId) {
        return service.list(profileId);
    }

    @GetMapping("/summary")
    public ReviewSummary summary(@PathVariable UUID profileId, @AuthenticationPrincipal Jwt jwt) {
        return service.summary(profileId, viewer(jwt));
    }

    @GetMapping("/overview")
    public ReviewOverview overview(@PathVariable UUID profileId, @AuthenticationPrincipal Jwt jwt) {
        return service.overview(profileId, viewer(jwt));
    }

    @PutMapping("/me")
    public ReviewResponse upsert(@PathVariable UUID profileId, @AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody ReviewRequests.Upsert request) {
        return service.upsert(profileId, me(jwt), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID profileId, @AuthenticationPrincipal Jwt jwt) {
        service.delete(profileId, me(jwt));
    }

    private UUID me(Jwt jwt) {
        if (jwt == null) throw new IllegalArgumentException("Autenticación requerida");
        return UUID.fromString(jwt.getSubject());
    }

    private UUID viewer(Jwt jwt) {
        return jwt == null ? null : UUID.fromString(jwt.getSubject());
    }
}
