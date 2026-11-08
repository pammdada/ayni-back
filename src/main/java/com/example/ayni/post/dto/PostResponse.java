package com.example.ayni.post.dto;

import com.example.ayni.post.model.Post;
import com.example.ayni.post.model.PostMedia;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        UUID communityId,
        String kind,
        String title,
        String content,
        String visibility,
        String status,
        Instant createdAt,
        Instant updatedAt,
        List<MediaResponse> media,
        long likeCount,
        long commentCount,
        boolean likedByMe
) {
    public record MediaResponse(UUID id, String url, String mediaType, int position) {
        public static MediaResponse from(PostMedia media) {
            return new MediaResponse(media.getId(), media.getUrl(), media.getMediaType().name(), media.getPosition());
        }
    }

    public static PostResponse from(Post post, List<PostMedia> media, long likeCount, long commentCount, boolean likedByMe) {
        return new PostResponse(
                post.getId(),
                post.getAuthorId(),
                post.getCommunityId(),
                post.getKind().name(),
                post.getTitle(),
                post.getContent(),
                post.getVisibility().name(),
                post.getStatus().name(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                media.stream().map(MediaResponse::from).toList(),
                likeCount,
                commentCount,
                likedByMe
        );
    }
}
