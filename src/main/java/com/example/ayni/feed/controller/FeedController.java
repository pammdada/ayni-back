package com.example.ayni.feed.controller;

import com.example.ayni.feed.dto.FeedItem;
import com.example.ayni.feed.service.FeedService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService service;

    public FeedController(FeedService service) {
        this.service = service;
    }

    @GetMapping
    public List<FeedItem> feed(@AuthenticationPrincipal Jwt jwt,
                               @RequestParam(required = false) Instant before,
                               @RequestParam(defaultValue = "20") int limit) {
        return service.feed(UUID.fromString(jwt.getSubject()), before, limit);
    }
}
