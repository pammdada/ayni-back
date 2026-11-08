package com.example.ayni.community.service;

import com.example.ayni.community.dto.*;
import com.example.ayni.community.model.*;
import com.example.ayni.community.repository.*;
import com.example.ayni.profile.dto.ProfileSummary;
import com.example.ayni.profile.model.Profile;
import com.example.ayni.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityService {
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final ProfileRepository profiles;

    public CommunityService(CommunityRepository communities, CommunityMemberRepository members,
                            ProfileRepository profiles) {
        this.communities = communities;
        this.members = members;
        this.profiles = profiles;
    }

    public CommunityResponse create(UUID user, CommunityRequests.Create request) {
        Community.Visibility visibility = request.visibility() == null
                ? Community.Visibility.PUBLIC
                : request.visibility();
        Community c = communities.save(new Community(request.name(), request.description(), request.locationName(),
                request.latitude(), request.longitude(), user, visibility, request.coverUrl()));
        members.save(new CommunityMember(c.getId(), user, CommunityMember.Role.ADMIN, CommunityMember.Status.ACTIVE));
        return CommunityResponse.from(c, 1L);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> list(Community.Visibility visibility) {
        List<Community> found = visibility == null
                ? communities.findAll()
                : communities.findByVisibility(visibility);
        Map<UUID, Long> counts = activeMemberCounts(found);
        return found.stream()
                .map(c -> CommunityResponse.from(c, counts.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public CommunityResponse get(UUID id) {
        Community c = community(id);
        return CommunityResponse.from(c, activeMemberCount(id));
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> mine(UUID user) {
        List<UUID> ids = members.findByUserIdAndStatus(user, CommunityMember.Status.ACTIVE)
                .stream()
                .map(CommunityMember::getCommunityId)
                .toList();
        if (ids.isEmpty()) return List.of();
        List<Community> found = communities.findByIdIn(ids);
        Map<UUID, Long> counts = activeMemberCounts(found);
        return found.stream()
                .map(c -> CommunityResponse.from(c, counts.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    public CommunityMemberResponse join(UUID id, UUID user) {
        Community community = community(id);
        Optional<CommunityMember> existing = members.findByCommunityIdAndUserId(id, user);
        if (existing.isPresent()) {
            CommunityMember.Status status = existing.get().getStatus();
            if (status == CommunityMember.Status.ACTIVE || status == CommunityMember.Status.PENDING) {
                throw new IllegalArgumentException("La solicitud ya existe");
            }
            throw new IllegalArgumentException("No puede unirse a esta comunidad");
        }
        CommunityMember.Status status = community.getVisibility() == Community.Visibility.PUBLIC
                ? CommunityMember.Status.ACTIVE
                : CommunityMember.Status.PENDING;
        CommunityMember saved = members.save(new CommunityMember(id, user, CommunityMember.Role.MEMBER, status));
        return CommunityMemberResponse.from(saved, profileSummary(user));
    }

    @Transactional(readOnly = true)
    public Optional<CommunityMemberResponse> membership(UUID id, UUID user) {
        community(id);
        return members.findByCommunityIdAndUserId(id, user)
                .map(member -> CommunityMemberResponse.from(member, profileSummary(member.getUserId())));
    }

    public void decide(UUID id, UUID requester, UUID admin, CommunityRequests.Membership decision) {
        requireAdmin(id, admin);
        CommunityMember member = members.findByCommunityIdAndUserId(id, requester)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));
        if (decision.approved()) member.approve();
        else member.reject();
    }

    @Transactional(readOnly = true)
    public List<CommunityMemberResponse> members(UUID id, CommunityMember.Status status) {
        community(id);
        List<CommunityMember> found = status == null
                ? members.findByCommunityId(id)
                : members.findByCommunityIdAndStatus(id, status);
        return memberResponses(found);
    }

    public void leave(UUID id, UUID user) {
        Community community = community(id);
        CommunityMember member = members.findByCommunityIdAndUserId(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));
        if (member.getRole() == CommunityMember.Role.ADMIN && community.getCreatedBy().equals(user)) {
            throw new IllegalArgumentException("El creador no puede abandonar la comunidad; transfiera la administración");
        }
        members.delete(member);
    }

    private List<CommunityMemberResponse> memberResponses(List<CommunityMember> found) {
        if (found.isEmpty()) return List.of();
        Set<UUID> userIds = found.stream().map(CommunityMember::getUserId).collect(Collectors.toSet());
        Map<UUID, ProfileSummary> profilesById = profiles.findAllById(userIds).stream()
                .collect(Collectors.toMap(Profile::getId, ProfileSummary::from));
        return found.stream()
                .map(member -> CommunityMemberResponse.from(member, profilesById.get(member.getUserId())))
                .toList();
    }

    private ProfileSummary profileSummary(UUID id) {
        return profiles.findById(id).map(ProfileSummary::from).orElse(null);
    }

    private long activeMemberCount(UUID id) {
        return members.countByCommunityIdAndStatus(id, CommunityMember.Status.ACTIVE);
    }

    private Map<UUID, Long> activeMemberCounts(List<Community> found) {
        List<UUID> ids = found.stream().map(Community::getId).toList();
        if (ids.isEmpty()) return Map.of();
        Map<UUID, Long> counts = new HashMap<>();
        for (Object[] row : members.countByCommunityIds(ids, CommunityMember.Status.ACTIVE)) {
            counts.put((UUID) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }

    private Community community(UUID id) {
        return communities.findById(id).orElseThrow(() -> new EntityNotFoundException("Comunidad no encontrada"));
    }

    private void requireAdmin(UUID id, UUID user) {
        CommunityMember member = members.findByCommunityIdAndUserId(id, user)
                .orElseThrow(() -> new IllegalArgumentException("No pertenece a la comunidad"));
        if (member.getRole() != CommunityMember.Role.ADMIN || member.getStatus() != CommunityMember.Status.ACTIVE) {
            throw new IllegalArgumentException("Se requiere ser administrador");
        }
    }
}
