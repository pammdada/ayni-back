package com.example.ayni.listing.repository;

import com.example.ayni.listing.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findByStatusNot(Listing.Status status);

    List<Listing> findByOwnerIdAndStatusNot(UUID owner, Listing.Status status);
}
