package com.example.ayni.post.repository;

import com.example.ayni.post.model.PostLike;
import com.example.ayni.post.model.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    long countByPostId(UUID postId);

    void deleteByPostIdAndUserId(UUID postId, UUID userId);

    @Query("select pl.postId, count(pl) from PostLike pl where pl.postId in :postIds group by pl.postId")
    List<Object[]> countByPostIds(@Param("postIds") Collection<UUID> postIds);
}
