package com.example.ayni;

import com.example.ayni.community.dto.CommunityRequests;
import com.example.ayni.community.dto.CommunityResponse;
import com.example.ayni.community.model.Community;
import com.example.ayni.community.service.CommunityService;
import com.example.ayni.feed.dto.FeedItem;
import com.example.ayni.feed.service.FeedService;
import com.example.ayni.follow.service.FollowService;
import com.example.ayni.post.dto.PostRequests;
import com.example.ayni.post.dto.PostResponse;
import com.example.ayni.post.model.Post;
import com.example.ayni.post.service.PostService;
import com.example.ayni.profile.model.AccountType;
import com.example.ayni.profile.model.Profile;
import com.example.ayni.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SocialFlowTests {
    @Autowired
    private ProfileRepository profiles;
    @Autowired
    private CommunityService communities;
    @Autowired
    private PostService posts;
    @Autowired
    private FollowService follows;
    @Autowired
    private FeedService feed;

    private UUID author;
    private UUID follower;
    private UUID stranger;

    @BeforeEach
    void setUp() {
        author = profile("author");
        follower = profile("follower");
        stranger = profile("stranger");
    }

    @Test
    void feedIncludesOwnFollowedAndHidesFollowersPostsFromStrangers() {
        PostResponse publicPost = posts.create(author, request(null, Post.Visibility.PUBLIC));
        PostResponse followersPost = posts.create(author, request(null, Post.Visibility.FOLLOWERS));

        follows.follow(follower, author);

        List<FeedItem> followerFeed = feed.feed(follower, null, 20);
        assertThat(followerFeed).extracting(FeedItem::id)
                .contains(publicPost.id(), followersPost.id());

        List<FeedItem> strangerFeed = feed.feed(stranger, null, 20);
        assertThat(strangerFeed).extracting(FeedItem::id)
                .contains(publicPost.id())
                .doesNotContain(followersPost.id());
    }

    @Test
    void privateCommunityPostsRequireActiveMembership() {
        CommunityResponse community = communities.create(author, new CommunityRequests.Create(
                "Grupo privado", "Solo miembros", null, null, null, Community.Visibility.PRIVATE, null));

        communities.join(community.id(), follower);

        PostResponse post = posts.create(author, new PostRequests.Create(
                community.id(), Post.Kind.POST, "Interno", "Contenido privado",
                Post.Visibility.PUBLIC, null));

        assertThatThrownBy(() -> posts.get(post.id(), follower))
                .isInstanceOf(EntityNotFoundException.class);

        communities.decide(community.id(), follower, author, new CommunityRequests.Membership(true));

        assertThat(posts.get(post.id(), follower).id()).isEqualTo(post.id());
    }

    @Test
    void likesAndCommentsAreCounted() {
        PostResponse post = posts.create(author, request(null, Post.Visibility.PUBLIC));

        assertThat(posts.like(post.id(), follower)).isEqualTo(1);
        posts.comment(post.id(), follower, new PostRequests.Comment("Hola", null));

        PostResponse updated = posts.get(post.id(), author);
        assertThat(updated.likeCount()).isEqualTo(1);
        assertThat(updated.commentCount()).isEqualTo(1);
        assertThat(updated.likedByMe()).isFalse();
        assertThat(posts.get(post.id(), follower).likedByMe()).isTrue();
    }

    private PostRequests.Create request(UUID communityId, Post.Visibility visibility) {
        return new PostRequests.Create(communityId, Post.Kind.POST, "Titulo", "Contenido de prueba",
                visibility, List.of(new PostRequests.Media("https://example.com/image.jpg", null)));
    }

    private UUID profile(String prefix) {
        UUID id = UUID.randomUUID();
        String suffix = id.toString().substring(0, 8);
        profiles.saveAndFlush(new Profile(id, AccountType.INDIVIDUAL, prefix + " " + suffix,
                prefix + "_" + suffix, prefix + "_" + suffix + "@example.com"));
        return id;
    }
}
