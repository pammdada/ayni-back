package com.example.ayni.listing.service;

import com.example.ayni.listing.dto.*;
import com.example.ayni.listing.model.*;
import com.example.ayni.listing.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ListingService {
    private final ListingRepository listings;
    private final CategoryRepository categories;

    public ListingService(ListingRepository listings, CategoryRepository categories) {
        this.listings = listings;
        this.categories = categories;
    }

    public ListingResponse create(UUID owner, ListingRequests.Create r) {
        if (!categories.existsById(r.categoryId())) throw new EntityNotFoundException("Categoría no encontrada");
        return ListingResponse.from(listings.save(new Listing(owner, r.communityId(), r.categoryId(), r.type(), r.title(), r.description(), r.price(), r.currency(), r.imageUrl())));
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> list(UUID community, UUID category) {
        return listings.findByStatusNot(Listing.Status.DELETED).stream().filter(l -> community == null || community.equals(l.getCommunityId())).filter(l -> category == null || category.equals(l.getCategoryId())).map(ListingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ListingResponse get(UUID id) {
        return ListingResponse.from(listing(id));
    }

    public ListingResponse update(UUID id, UUID user, ListingRequests.Update r) {
        Listing l = owned(id, user);
        l.update(r.type(), r.title(), r.description(), r.price(), r.currency(), r.imageUrl(), r.status());
        return ListingResponse.from(l);
    }

    public void delete(UUID id, UUID user) {
        owned(id, user).deactivate();
    }

    @Transactional(readOnly = true)
    public List<Category> categories() {
        return categories.findAll();
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> byProfile(UUID user) {
        return listings.findByOwnerIdAndStatusNot(user, Listing.Status.DELETED).stream().map(ListingResponse::from).toList();
    }

    private Listing listing(UUID id) {
        return listings.findById(id).orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));
    }

    private Listing owned(UUID id, UUID user) {
        Listing l = listing(id);
        if (!l.getOwnerId().equals(user)) throw new IllegalArgumentException("No puede modificar esta publicación");
        return l;
    }
}
