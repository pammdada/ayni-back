package com.example.ayni.post.dto;

import com.example.ayni.post.model.Post;
import com.example.ayni.post.model.PostMedia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public final class PostRequests {
    private PostRequests() {
    }

    public record Create(
            UUID communityId,
            Post.Kind kind,
            @Size(max = 160) String title,
            @NotBlank @Size(max = 4000) String content,
            Post.Visibility visibility,
            @Valid @Size(max = 10) List<Media> media
    ) {
    }

    public record Update(
            Post.Kind kind,
            @Size(max = 160) String title,
            @NotBlank @Size(max = 4000) String content,
            Post.Visibility visibility,
            @Valid @Size(max = 10) List<Media> media
    ) {
    }

    public record Media(
            @NotBlank @Size(max = 2048) String url,
            PostMedia.MediaType type
    ) {
    }

    public record Comment(
            @NotBlank @Size(max = 2000) String content,
            UUID parentId
    ) {
    }
}
