package com.example.ayni.help.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "help_responses")
public class HelpResponse {
    @Id
    private UUID id;
    @Column(name = "help_post_id")
    private UUID helpPostId;
    @Column(name = "responder_id")
    private UUID responderId;
    private String message;
    private String status;

    protected HelpResponse() {
    }

    public HelpResponse(UUID post, UUID responder, String message) {
        id = UUID.randomUUID();
        helpPostId = post;
        responderId = responder;
        this.message = message;
        status = "PENDING";
    }
}
