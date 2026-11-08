package com.example.ayni.help.controller;

import com.example.ayni.help.dto.*;
import com.example.ayni.help.service.HelpService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/help-posts")
public class HelpController {
    private final HelpService service;

    public HelpController(HelpService service) {
        this.service = service;
    }

    private UUID me(Jwt j) {
        return UUID.fromString(j.getSubject());
    }

    @PostMapping
    public ResponseEntity<HelpPostResponse> create(@AuthenticationPrincipal Jwt j, @Valid @RequestBody HelpRequests.Create r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(me(j), r));
    }

    @GetMapping
    public List<HelpPostResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public HelpPostResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/{id}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public void respond(@PathVariable UUID id, @AuthenticationPrincipal Jwt j, @Valid @RequestBody HelpRequests.Response r) {
        service.respond(id, me(j), r);
    }

    @PutMapping("/{id}/status")
    public HelpPostResponse status(@PathVariable UUID id, @AuthenticationPrincipal Jwt j, @Valid @RequestBody HelpRequests.Status r) {
        return service.status(id, me(j), r);
    }
}
