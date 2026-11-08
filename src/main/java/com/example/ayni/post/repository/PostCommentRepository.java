package com.example.ayni.post.repository;

import com.example.ayni.post.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {
    List<PostComment> findByPostIdAndStatusOrderByCreatedAtAsc(UUID postId, PostComment.Status status);

    long countByPostIdAndStatus(UUID postId, PostComment.Status status);

    @Query("""
            select c.postId, count(c) from PostComment c
            where c.postId in :postIds and c.status = :status
            group by c.postId
            """)
    List<Object[]> countByPostIds(@Param("postIds") Collection<UUID> postIds,
                                  @Param("status") PostComment.Status status);
}
