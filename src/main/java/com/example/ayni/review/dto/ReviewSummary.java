package com.example.ayni.review.dto;

import java.util.List;

public record ReviewSummary(
        double average,
        long count,
        List<TaskRating> byTask,
        ReviewResponse myReview
) {
    public record TaskRating(String task, double average, long count) {
    }
}
