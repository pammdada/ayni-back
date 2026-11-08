package com.example.ayni.follow.controller;

import com.example.ayni.follow.service.FollowService;
import com.example.ayni.profile.dto.ProfileSummary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class FollowController {
    private final FollowService service;

    public FollowController(FollowService service) {
        this.service = service;
    }

    private UUID me(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping("/api/profiles/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.follow(me(jwt), id);
    }

    @DeleteMapping("/api/profiles/{id}/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.unfollow(me(jwt), id);
    }

    @GetMapping("/api/profiles/{id}/followers")
    public List<ProfileSummary> followers(@PathVariable UUID id) {
        return service.followers(id);
    }

    @GetMapping("/api/profiles/{id}/following")
    public List<ProfileSummary> following(@PathVariable UUID id) {
        return service.following(id);
    }
}
