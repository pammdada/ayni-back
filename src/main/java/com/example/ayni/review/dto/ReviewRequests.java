package com.example.ayni.review.dto;

import jakarta.validation.constraints.*;

public final class ReviewRequests {
    private ReviewRequests() {
    }

    public record Upsert(
            @NotNull @Min(1) @Max(5) Integer stars,
            @NotBlank @Size(max = 120) String task,
            @Size(max = 1000) String comment
    ) {
    }
}
