package com.example.ayni.sync.service;

import com.example.ayni.community.service.CommunityService;
import com.example.ayni.follow.service.FollowService;
import com.example.ayni.help.dto.HelpPostResponse;
import com.example.ayni.help.dto.HelpRequests;
import com.example.ayni.help.service.HelpService;
import com.example.ayni.listing.dto.ListingRequests;
import com.example.ayni.listing.dto.ListingResponse;
import com.example.ayni.listing.service.ListingService;
import com.example.ayni.post.dto.CommentResponse;
import com.example.ayni.post.dto.PostRequests;
import com.example.ayni.post.dto.PostResponse;
import com.example.ayni.post.service.PostService;
import com.example.ayni.sync.dto.SyncRequests;
import com.example.ayni.sync.model.SyncOperation;
import com.example.ayni.sync.repository.SyncOperationRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SyncService {

    private final SyncOperationRepository operations;
    private final ListingService listings;
    private final HelpService help;
    private final PostService posts;
    private final FollowService follows;
    private final CommunityService communities;
    private final ObjectMapper json;

    public SyncService(
            SyncOperationRepository operations,
            ListingService listings,
            HelpService help,
            PostService posts,
            FollowService follows,
            CommunityService communities,
            ObjectMapper json
    ) {
        this.operations = operations;
        this.listings = listings;
        this.help = help;
        this.posts = posts;
        this.follows = follows;
        this.communities = communities;
        this.json = json;
    }

    public List<SyncRequests.Result> sync(
            UUID user,
            SyncRequests.Batch batch
    ) {
        return batch.operations()
                .stream()
                .map(op -> process(user, op))
                .toList();
    }

    private SyncRequests.Result process(
            UUID user,
            SyncRequests.Operation input
    ) {
        var previous = operations.findByOperationId(input.operationId());

        if (previous.isPresent()) {
            var p = previous.get();

            return new SyncRequests.Result(
                    p.getOperationId(),
                    p.getStatus(),
                    p.getEntityId()
            );
        }

        SyncOperation saved = operations.save(
                new SyncOperation(
                        user,
                        input.operationId(),
                        input.operationType()
                )
        );

        try {
            UUID entity = switch (input.operationType()) {
                case "CREATE_LISTING" -> listing(user, input);
                case "CREATE_HELP_POST" -> help(user, input);
                case "CREATE_POST" -> post(user, input);
                case "LIKE_POST" -> likePost(user, input);
                case "UNLIKE_POST" -> unlikePost(user, input);
                case "COMMENT_POST" -> commentPost(user, input);
                case "FOLLOW_PROFILE" -> followProfile(user, input);
                case "UNFOLLOW_PROFILE" -> unfollowProfile(user, input);
                case "JOIN_COMMUNITY" -> joinCommunity(user, input);
                default -> throw new IllegalArgumentException(
                        "Tipo de operación no soportado"
                );
            };

            saved.complete(entity);

            return new SyncRequests.Result(
                    input.operationId(),
                    "SYNCED",
                    entity
            );

        } catch (RuntimeException e) {
            saved.fail();
            throw e;
        }
    }

    private UUID listing(UUID user, SyncRequests.Operation op) {
        ListingRequests.Create request =
                json.convertValue(op.data(), ListingRequests.Create.class);

        ListingResponse created = listings.create(user, request);

        return created.id();
    }

    private UUID help(UUID user, SyncRequests.Operation op) {
        HelpRequests.Create request =
                json.convertValue(op.data(), HelpRequests.Create.class);

        HelpPostResponse created = help.create(user, request);

        return created.id();
    }

    private UUID post(UUID user, SyncRequests.Operation op) {
        PostRequests.Create request =
                json.convertValue(op.data(), PostRequests.Create.class);

        PostResponse created = posts.create(user, request);

        return created.id();
    }

    private UUID likePost(UUID user, SyncRequests.Operation op) {
        SyncRequests.LikePost request =
                json.convertValue(op.data(), SyncRequests.LikePost.class);
        posts.like(request.postId(), user);
        return request.postId();
    }

    private UUID unlikePost(UUID user, SyncRequests.Operation op) {
        SyncRequests.LikePost request =
                json.convertValue(op.data(), SyncRequests.LikePost.class);
        posts.unlike(request.postId(), user);
        return request.postId();
    }

    private UUID commentPost(UUID user, SyncRequests.Operation op) {
        SyncRequests.CommentPost request =
                json.convertValue(op.data(), SyncRequests.CommentPost.class);
        CommentResponse created = posts.comment(request.postId(), user,
                new PostRequests.Comment(request.content(), request.parentId()));
        return created.id();
    }

    private UUID followProfile(UUID user, SyncRequests.Operation op) {
        SyncRequests.FollowProfile request =
                json.convertValue(op.data(), SyncRequests.FollowProfile.class);
        follows.follow(user, request.followedId());
        return request.followedId();
    }

    private UUID unfollowProfile(UUID user, SyncRequests.Operation op) {
        SyncRequests.FollowProfile request =
                json.convertValue(op.data(), SyncRequests.FollowProfile.class);
        follows.unfollow(user, request.followedId());
        return request.followedId();
    }

    private UUID joinCommunity(UUID user, SyncRequests.Operation op) {
        SyncRequests.JoinCommunity request =
                json.convertValue(op.data(), SyncRequests.JoinCommunity.class);
        communities.join(request.communityId(), user);
        return request.communityId();
    }
}
