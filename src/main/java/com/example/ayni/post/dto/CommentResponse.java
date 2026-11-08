package com.example.ayni.post.dto;

import com.example.ayni.post.model.PostComment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        UUID authorId,
        UUID parentId,
        String content,
        String status,
        Instant createdAt
) {
    public static CommentResponse from(PostComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getAuthorId(),
                comment.getParentId(),
                comment.getContent(),
                comment.getStatus().name(),
                comment.getCreatedAt()
        );
    }
}
