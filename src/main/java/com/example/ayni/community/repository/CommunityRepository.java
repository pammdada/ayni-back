package com.example.ayni.community.repository;

import com.example.ayni.community.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID> {
    List<Community> findByIdIn(Collection<UUID> ids);

    List<Community> findByVisibility(Community.Visibility visibility);
}
