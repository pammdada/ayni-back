package com.example.ayni.listing.dto;

import com.example.ayni.listing.model.Listing;

import java.math.BigDecimal;
import java.util.UUID;

public record ListingResponse(UUID id, UUID ownerId, UUID communityId, UUID categoryId, String type, String title,
                              String description, BigDecimal price, String currency, String imageUrl, String status) {
    public static ListingResponse from(Listing l) {
        return new ListingResponse(l.getId(), l.getOwnerId(), l.getCommunityId(), l.getCategoryId(), l.getType().name(), l.getTitle(), l.getDescription(), l.getPrice(), l.getCurrency(), l.getImageUrl(), l.getStatus().name());
    }
}
