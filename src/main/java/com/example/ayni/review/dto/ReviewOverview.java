package com.example.ayni.review.dto;

import java.util.List;

/**
 * Combina el resumen y el listado de reseñas para que el frontend no tenga
 * que pedirlos por separado (y el backend no cargue dos veces las mismas filas).
 */
public record ReviewOverview(ReviewSummary summary, List<ReviewResponse> reviews) {
}
