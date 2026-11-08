
package com.example.ayni.sync.dto;

import tools.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public final class SyncRequests {

    private SyncRequests() {
    }

    public record Batch(
            @NotEmpty
            List<Operation> operations
    ) {
    }

    public record Operation(
            @NotNull
            UUID operationId,

            @NotBlank
            String operationType,

            @NotNull
            JsonNode data
    ) {
    }

    public record Result(
            UUID operationId,
            String status,
            UUID entityId
    ) {
    }
}
