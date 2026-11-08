package com.example.ayni.post.repository;

import com.example.ayni.post.model.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {
    List<PostMedia> findByPostIdOrderByPositionAsc(UUID postId);

    List<PostMedia> findByPostIdInOrderByPositionAsc(Collection<UUID> postIds);

    void deleteByPostId(UUID postId);
}
