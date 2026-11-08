package com.example.ayni.community.service;

import com.example.ayni.community.dto.*;
import com.example.ayni.community.model.*;
import com.example.ayni.community.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CommunityService {
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;

    public CommunityService(CommunityRepository communities, CommunityMemberRepository members) {
        this.communities = communities;
        this.members = members;
    }

    public CommunityResponse create(UUID user, CommunityRequests.Create request) {
        Community.Visibility visibility = request.visibility() == null
                ? Community.Visibility.PUBLIC
                : request.visibility();
        Community c = communities.save(new Community(request.name(), request.description(), request.locationName(),
                request.latitude(), request.longitude(), user, visibility, request.coverUrl()));
        members.save(new CommunityMember(c.getId(), user, CommunityMember.Role.ADMIN, CommunityMember.Status.ACTIVE));
        return CommunityResponse.from(c);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> list(Community.Visibility visibility) {
        List<Community> found = visibility == null
                ? communities.findAll()
                : communities.findByVisibility(visibility);
        return found.stream().map(CommunityResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CommunityResponse get(UUID id) {
        return CommunityResponse.from(community(id));
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> mine(UUID user) {
        List<UUID> ids = members.findByUserIdAndStatus(user, CommunityMember.Status.ACTIVE)
                .stream()
                .map(CommunityMember::getCommunityId)
                .toList();
        if (ids.isEmpty()) return List.of();
        return communities.findByIdIn(ids).stream().map(CommunityResponse::from).toList();
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
        return CommunityMemberResponse.from(
                members.save(new CommunityMember(id, user, CommunityMember.Role.MEMBER, status)));
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
        return found.stream().map(CommunityMemberResponse::from).toList();
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
