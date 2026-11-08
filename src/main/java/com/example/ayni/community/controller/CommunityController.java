package com.example.ayni.community.controller;

import com.example.ayni.community.dto.*;
import com.example.ayni.community.model.Community;
import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.community.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {
    private final CommunityService service;

    public CommunityController(CommunityService service) {
        this.service = service;
    }

    private UUID me(Jwt j) {
        return UUID.fromString(j.getSubject());
    }

    @PostMapping
    public ResponseEntity<CommunityResponse> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CommunityRequests.Create r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(me(jwt), r));
    }

    @GetMapping
    public List<CommunityResponse> list(@RequestParam(required = false) Community.Visibility visibility) {
        return service.list(visibility);
    }

    @GetMapping("/mine")
    public List<CommunityResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return service.mine(me(jwt));
    }

    @GetMapping("/{id}")
    public CommunityResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommunityMemberResponse join(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.join(id, me(jwt));
    }

    @GetMapping("/{id}/members")
    public List<CommunityMemberResponse> members(@PathVariable UUID id,
                                                 @RequestParam(required = false) CommunityMember.Status status) {
        return service.members(id, status);
    }

    @PutMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decide(@PathVariable UUID id, @PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CommunityRequests.Membership r) {
        service.decide(id, userId, me(jwt), r);
    }

    @DeleteMapping("/{id}/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.leave(id, me(jwt));
    }
}
