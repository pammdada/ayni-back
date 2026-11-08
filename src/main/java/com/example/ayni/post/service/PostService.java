package com.example.ayni.post.service;

import com.example.ayni.community.model.Community;
import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.community.repository.CommunityMemberRepository;
import com.example.ayni.community.repository.CommunityRepository;
import com.example.ayni.follow.repository.ProfileFollowRepository;
import com.example.ayni.post.dto.CommentResponse;
import com.example.ayni.post.dto.PostRequests;
import com.example.ayni.post.dto.PostResponse;
import com.example.ayni.post.model.Post;
import com.example.ayni.post.model.PostComment;
import com.example.ayni.post.model.PostLike;
import com.example.ayni.post.model.PostMedia;
import com.example.ayni.post.repository.PostCommentRepository;
import com.example.ayni.post.repository.PostLikeRepository;
import com.example.ayni.post.repository.PostMediaRepository;
import com.example.ayni.post.repository.PostRepository;
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
public class PostService {
    private final PostRepository posts;
    private final PostMediaRepository media;
    private final PostLikeRepository likes;
    private final PostCommentRepository comments;
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final ProfileFollowRepository follows;
    private final ProfileRepository profiles;

    public PostService(PostRepository posts, PostMediaRepository media, PostLikeRepository likes,
                       PostCommentRepository comments, CommunityRepository communities,
                       CommunityMemberRepository members, ProfileFollowRepository follows,
                       ProfileRepository profiles) {
        this.posts = posts;
        this.media = media;
        this.likes = likes;
        this.comments = comments;
        this.communities = communities;
        this.members = members;
        this.follows = follows;
        this.profiles = profiles;
    }

    public PostResponse create(UUID user, PostRequests.Create request) {
        Post.Kind kind = request.kind() == null ? Post.Kind.POST : request.kind();
        Post.Visibility visibility = request.visibility() == null ? Post.Visibility.PUBLIC : request.visibility();

        if (visibility == Post.Visibility.COMMUNITY && request.communityId() == null) {
            throw new IllegalArgumentException("La visibilidad COMMUNITY requiere una comunidad");
        }
        if (request.communityId() != null) {
            community(request.communityId());
            requireActiveMember(request.communityId(), user);
            if (kind == Post.Kind.ANNOUNCEMENT) requireAdmin(request.communityId(), user);
        }

        Post post = posts.save(new Post(user, request.communityId(), kind, request.title(), request.content(), visibility));
        replaceMedia(post.getId(), request.media());
        return response(post, user);
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID id, UUID viewer) {
        return response(visible(id, viewer), viewer);
    }

    public PostResponse update(UUID id, UUID user, PostRequests.Update request) {
        Post post = owned(id, user);
        Post.Kind kind = request.kind() == null ? post.getKind() : request.kind();
        Post.Visibility visibility = request.visibility() == null ? post.getVisibility() : request.visibility();

        if (visibility == Post.Visibility.COMMUNITY && post.getCommunityId() == null) {
            throw new IllegalArgumentException("La visibilidad COMMUNITY requiere una comunidad");
        }
        if (post.getCommunityId() != null && kind == Post.Kind.ANNOUNCEMENT) {
            requireAdmin(post.getCommunityId(), user);
        }

        post.update(kind, request.title(), request.content(), visibility);
        if (request.media() != null) replaceMedia(post.getId(), request.media());
        return response(post, user);
    }

    public void delete(UUID id, UUID user) {
        owned(id, user).delete();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> byProfile(UUID profile, UUID viewer) {
        return posts.findByAuthorIdAndStatusNotOrderByCreatedAtDesc(profile, Post.Status.DELETED)
                .stream()
                .filter(post -> canView(post, viewer))
                .map(post -> response(post, viewer))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> byCommunity(UUID communityId, UUID viewer) {
        Community community = community(communityId);
        if (community.getVisibility() == Community.Visibility.PRIVATE && !isActiveMember(communityId, viewer)) {
            throw new EntityNotFoundException("Comunidad no encontrada");
        }
        return posts.findByCommunityIdAndStatusNotOrderByCreatedAtDesc(communityId, Post.Status.DELETED)
                .stream()
                .filter(post -> canView(post, viewer))
                .map(post -> response(post, viewer))
                .toList();
    }

    public long like(UUID id, UUID user) {
        visible(id, user);
        if (!likes.existsByPostIdAndUserId(id, user)) likes.save(new PostLike(id, user));
        return likes.countByPostId(id);
    }

    public long unlike(UUID id, UUID user) {
        visible(id, user);
        likes.deleteByPostIdAndUserId(id, user);
        return likes.countByPostId(id);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> comments(UUID id, UUID viewer) {
        visible(id, viewer);
        return commentResponses(comments.findByPostIdAndStatusOrderByCreatedAtAsc(id, PostComment.Status.ACTIVE));
    }

    public CommentResponse comment(UUID id, UUID user, PostRequests.Comment request) {
        visible(id, user);
        if (request.parentId() != null) {
            PostComment parent = comment(request.parentId());
            if (!parent.getPostId().equals(id) || parent.getStatus() != PostComment.Status.ACTIVE) {
                throw new IllegalArgumentException("El comentario padre no pertenece a la publicación");
            }
        }
        PostComment saved = comments.save(new PostComment(id, user, request.parentId(), request.content()));
        return CommentResponse.from(saved, profileSummary(user));
    }

    public void deleteComment(UUID commentId, UUID user) {
        PostComment comment = comment(commentId);
        if (!comment.getAuthorId().equals(user)) {
            throw new IllegalArgumentException("No puede eliminar este comentario");
        }
        comment.delete();
    }

    private void replaceMedia(UUID postId, List<PostRequests.Media> items) {
        if (items == null) return;
        media.deleteByPostId(postId);
        for (int i = 0; i < items.size(); i++) {
            PostRequests.Media item = items.get(i);
            PostMedia.MediaType type = item.type() == null ? PostMedia.MediaType.IMAGE : item.type();
            media.save(new PostMedia(postId, item.url(), type, i));
        }
    }

    private PostResponse response(Post post, UUID viewer) {
        List<PostMedia> postMedia = media.findByPostIdOrderByPositionAsc(post.getId());
        long likeCount = likes.countByPostId(post.getId());
        long commentCount = comments.countByPostIdAndStatus(post.getId(), PostComment.Status.ACTIVE);
        boolean likedByMe = likes.existsByPostIdAndUserId(post.getId(), viewer);
        return PostResponse.from(post, postMedia, likeCount, commentCount, likedByMe);
    }

    private List<CommentResponse> commentResponses(List<PostComment> found) {
        if (found.isEmpty()) return List.of();
        Set<UUID> authorIds = found.stream().map(PostComment::getAuthorId).collect(Collectors.toSet());
        Map<UUID, ProfileSummary> authors = profiles.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Profile::getId, ProfileSummary::from));
        return found.stream()
                .map(comment -> CommentResponse.from(comment, authors.get(comment.getAuthorId())))
                .toList();
    }

    private ProfileSummary profileSummary(UUID id) {
        return profiles.findById(id).map(ProfileSummary::from).orElse(null);
    }

    private Post visible(UUID id, UUID viewer) {
        Post post = post(id);
        if (!canView(post, viewer)) throw new EntityNotFoundException("Publicación no encontrada");
        return post;
    }

    private boolean canView(Post post, UUID viewer) {
        if (post.getStatus() == Post.Status.DELETED) return false;
        if (post.getStatus() == Post.Status.HIDDEN && !post.getAuthorId().equals(viewer)) return false;
        if (post.getAuthorId().equals(viewer)) return true;

        if (post.getCommunityId() != null) {
            Community community = community(post.getCommunityId());
            if (community.getVisibility() == Community.Visibility.PRIVATE) return isActiveMember(post.getCommunityId(), viewer);
        }

        return switch (post.getVisibility()) {
            case PUBLIC -> true;
            case FOLLOWERS -> follows.existsByFollowerIdAndFollowedId(viewer, post.getAuthorId());
            case COMMUNITY -> post.getCommunityId() != null && isActiveMember(post.getCommunityId(), viewer);
        };
    }

    private Post owned(UUID id, UUID user) {
        Post post = post(id);
        if (!post.getAuthorId().equals(user)) throw new IllegalArgumentException("No puede modificar esta publicación");
        return post;
    }

    private Post post(UUID id) {
        return posts.findById(id).orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));
    }

    private PostComment comment(UUID id) {
        return comments.findById(id).orElseThrow(() -> new EntityNotFoundException("Comentario no encontrado"));
    }

    private Community community(UUID id) {
        return communities.findById(id).orElseThrow(() -> new EntityNotFoundException("Comunidad no encontrada"));
    }

    private boolean isActiveMember(UUID communityId, UUID user) {
        return members.existsByCommunityIdAndUserIdAndStatus(communityId, user, CommunityMember.Status.ACTIVE);
    }

    private void requireActiveMember(UUID communityId, UUID user) {
        if (!isActiveMember(communityId, user)) {
            throw new IllegalArgumentException("Debe pertenecer a la comunidad para publicar");
        }
    }

    private void requireAdmin(UUID communityId, UUID user) {
        CommunityMember member = members.findByCommunityIdAndUserId(communityId, user)
                .orElseThrow(() -> new IllegalArgumentException("No pertenece a la comunidad"));
        if (member.getRole() != CommunityMember.Role.ADMIN || member.getStatus() != CommunityMember.Status.ACTIVE) {
            throw new IllegalArgumentException("Se requiere ser administrador");
        }
    }
}
