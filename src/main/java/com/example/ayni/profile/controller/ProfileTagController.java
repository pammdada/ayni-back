package com.example.ayni.profile.controller;

import com.example.ayni.profile.model.ProfileTag;
import com.example.ayni.profile.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profile-tags")
public class ProfileTagController {
    private final ProfileService service;

    public ProfileTagController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProfileTag> list() {
        return service.availableTags();
    }
}
