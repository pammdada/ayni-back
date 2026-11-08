package com.example.ayni.profile.controller;

import com.example.ayni.profile.dto.*;
import com.example.ayni.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequests.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(UUID.fromString(jwt.getSubject()), request));
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.get(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    public ProfileResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/me")
    public ProfileResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequests.Update request) {
        return service.update(UUID.fromString(jwt.getSubject()), request);
    }

    @PutMapping("/me/tags")
    public ProfileResponse tags(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequests.Tags request) {
        return service.updateTags(UUID.fromString(jwt.getSubject()), request);
    }
}
