package com.example.ayni.post.dto;

import com.example.ayni.post.model.PostComment;
import com.example.ayni.profile.dto.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        UUID authorId,
        ProfileSummary author,
        UUID parentId,
        String content,
        String status,
        Instant createdAt
) {
    public static CommentResponse from(PostComment comment, ProfileSummary author) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getAuthorId(),
                author,
                comment.getParentId(),
                comment.getContent(),
                comment.getStatus().name(),
                comment.getCreatedAt()
        );
    }
}
