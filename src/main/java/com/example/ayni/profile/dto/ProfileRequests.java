package com.example.ayni.profile.dto;

import com.example.ayni.profile.model.AccountType;
import jakarta.validation.constraints.*;

import java.util.Set;

public final class ProfileRequests {
    private ProfileRequests() {
    }

    public record Create(@NotNull AccountType accountType, @NotBlank @Size(max = 120) String fullName,
                         @NotBlank @Size(max = 50) String username, @NotBlank @Email String email,
                         @Size(max = 120) String sector) {
    }

    public record Update(@NotBlank @Size(max = 120) String fullName, @Size(max = 30) String phone,
                         @Size(max = 120) String sector, @Size(max = 500) String bio,
                         @Size(max = 2048) String avatarUrl) {
    }

    public record Tags(@NotNull Set<String> tags) {
    }
}
