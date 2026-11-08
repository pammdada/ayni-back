package com.example.ayni.follow.service;

import com.example.ayni.follow.model.ProfileFollow;
import com.example.ayni.follow.repository.ProfileFollowRepository;
import com.example.ayni.profile.dto.ProfileSummary;
import com.example.ayni.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FollowService {
    private final ProfileFollowRepository follows;
    private final ProfileRepository profiles;

    public FollowService(ProfileFollowRepository follows, ProfileRepository profiles) {
        this.follows = follows;
        this.profiles = profiles;
    }

    public void follow(UUID follower, UUID followed) {
        if (follower.equals(followed)) throw new IllegalArgumentException("No puede seguirse a sí mismo");
        if (!profiles.existsById(followed)) throw new EntityNotFoundException("Perfil no encontrado");
        if (!follows.existsByFollowerIdAndFollowedId(follower, followed)) {
            follows.save(new ProfileFollow(follower, followed));
        }
    }

    public void unfollow(UUID follower, UUID followed) {
        follows.deleteByFollowerIdAndFollowedId(follower, followed);
    }

    @Transactional(readOnly = true)
    public List<ProfileSummary> followers(UUID profile) {
        return summaries(follows.findFollowerIds(profile));
    }

    @Transactional(readOnly = true)
    public List<ProfileSummary> following(UUID profile) {
        return summaries(follows.findFollowedIds(profile));
    }

    private List<ProfileSummary> summaries(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        return profiles.findAllById(ids).stream().map(ProfileSummary::from).toList();
    }
}
