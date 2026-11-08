package com.example.ayni.post.controller;

import com.example.ayni.post.dto.CommentResponse;
import com.example.ayni.post.dto.PostRequests;
import com.example.ayni.post.dto.PostResponse;
import com.example.ayni.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    private UUID me(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping("/api/posts")
    public ResponseEntity<PostResponse> create(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody PostRequests.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(me(jwt), request));
    }

    @GetMapping("/api/posts/{id}")
    public PostResponse get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.get(id, me(jwt));
    }

    @PutMapping("/api/posts/{id}")
    public PostResponse update(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody PostRequests.Update request) {
        return service.update(id, me(jwt), request);
    }

    @DeleteMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, me(jwt));
    }

    @PostMapping("/api/posts/{id}/likes")
    public long like(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.like(id, me(jwt));
    }

    @DeleteMapping("/api/posts/{id}/likes")
    public long unlike(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.unlike(id, me(jwt));
    }

    @GetMapping("/api/posts/{id}/comments")
    public List<CommentResponse> comments(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.comments(id, me(jwt));
    }

    @PostMapping("/api/posts/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse comment(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt,
                                   @Valid @RequestBody PostRequests.Comment request) {
        return service.comment(id, me(jwt), request);
    }

    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.deleteComment(id, me(jwt));
    }

    @GetMapping("/api/profiles/{id}/posts")
    public List<PostResponse> byProfile(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.byProfile(id, me(jwt));
    }

    @GetMapping("/api/communities/{id}/posts")
    public List<PostResponse> byCommunity(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.byCommunity(id, me(jwt));
    }
}
