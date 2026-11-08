package com.example.ayni.help.dto;

import com.example.ayni.help.model.HelpPost;

import java.util.UUID;

public record HelpPostResponse(UUID id, UUID userId, String type, String title, String description, String status,
                               boolean emergency) {
    public static HelpPostResponse from(HelpPost h) {
        return new HelpPostResponse(h.getId(), h.getUserId(), h.getType().name(), h.getTitle(), h.getDescription(), h.getStatus().name(), h.isEmergency());
    }
}
