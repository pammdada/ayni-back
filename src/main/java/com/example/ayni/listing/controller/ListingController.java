package com.example.ayni.listing.controller;

import com.example.ayni.listing.dto.*;
import com.example.ayni.listing.model.Category;
import com.example.ayni.listing.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ListingController {
    private final ListingService service;

    public ListingController(ListingService service) {
        this.service = service;
    }

    private UUID me(Jwt j) {
        return UUID.fromString(j.getSubject());
    }

    @PostMapping("/api/listings")
    public ResponseEntity<ListingResponse> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ListingRequests.Create r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(me(jwt), r));
    }

    @GetMapping("/api/listings")
    public List<ListingResponse> list(@RequestParam(required = false) UUID communityId, @RequestParam(required = false) UUID categoryId) {
        return service.list(communityId, categoryId);
    }

    @GetMapping("/api/listings/{id}")
    public ListingResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/api/listings/{id}")
    public ListingResponse update(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ListingRequests.Update r) {
        return service.update(id, me(jwt), r);
    }

    @DeleteMapping("/api/listings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, me(jwt));
    }

    @GetMapping("/api/categories")
    public List<Category> categories() {
        return service.categories();
    }

    @GetMapping("/api/profiles/{id}/listings")
    public List<ListingResponse> userListings(@PathVariable UUID id) {
        return service.byProfile(id);
    }
}
