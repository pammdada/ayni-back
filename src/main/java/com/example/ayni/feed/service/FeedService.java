package com.example.ayni.feed.service;

import com.example.ayni.community.model.Community;
import com.example.ayni.community.model.CommunityMember;
import com.example.ayni.feed.dto.FeedItem;
import com.example.ayni.help.model.HelpPost;
import com.example.ayni.help.repository.HelpPostRepository;
import com.example.ayni.listing.model.Listing;
import com.example.ayni.listing.repository.ListingRepository;
import com.example.ayni.post.model.Post;
import com.example.ayni.post.model.PostMedia;
import com.example.ayni.post.repository.PostMediaRepository;
import com.example.ayni.post.repository.PostRepository;
import com.example.ayni.profile.dto.ProfileSummary;
import com.example.ayni.profile.repository.ProfileRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class FeedService {
    private static final int MAX_LIMIT = 50;
    private final PostRepository posts;
    private final PostMediaRepository media;
    private final ListingRepository listings;
    private final HelpPostRepository helps;
    private final ProfileRepository profiles;

    public FeedService(PostRepository posts, PostMediaRepository media, ListingRepository listings,
                       HelpPostRepository helps, ProfileRepository profiles) {
        this.posts = posts;
        this.media = media;
        this.listings = listings;
        this.helps = helps;
        this.profiles = profiles;
    }

    public List<FeedItem> feed(UUID user, Instant before, int limit) {
        int size = Math.max(1, Math.min(limit, MAX_LIMIT));
        Instant cursor = before == null ? Instant.now().plusSeconds(1) : before;
        PageRequest page = PageRequest.of(0, size);

        List<Post> postList = posts.findFeedFor(user, cursor, Post.Status.ACTIVE, Post.Visibility.PUBLIC,
                Post.Visibility.FOLLOWERS, Community.Visibility.PUBLIC, CommunityMember.Status.ACTIVE, page);
        List<Listing> listingList = listings.findFeedFor(user, cursor, Listing.Status.ACTIVE,
                CommunityMember.Status.ACTIVE, page);
        List<HelpPost> helpList = helps.findFeedFor(user, cursor, HelpPost.Status.OPEN,
                CommunityMember.Status.ACTIVE, page);

        Set<UUID> authorIds = new HashSet<>();
        postList.forEach(p -> authorIds.add(p.getAuthorId()));
        listingList.forEach(l -> authorIds.add(l.getOwnerId()));
        helpList.forEach(h -> authorIds.add(h.getUserId()));

        Map<UUID, ProfileSummary> authors = new HashMap<>();
        if (!authorIds.isEmpty()) {
            profiles.findAllById(authorIds).forEach(p -> authors.put(p.getId(), ProfileSummary.from(p)));
        }

        List<UUID> postIds = postList.stream().map(Post::getId).toList();
        Map<UUID, PostInteraction> interactions = new HashMap<>();
        if (!postIds.isEmpty()) {
            for (Object[] row : posts.interactionStats(postIds, user)) {
                interactions.put(UUID.fromString((String) row[0]), new PostInteraction(
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue() > 0));
            }
        }
        Map<UUID, String> images = new HashMap<>();
        if (!postIds.isEmpty()) {
            for (PostMedia item : media.findByPostIdInOrderByPositionAsc(postIds)) {
                images.putIfAbsent(item.getPostId(), item.getUrl());
            }
        }

        List<FeedItem> items = new ArrayList<>();
        for (Post post : postList) {
            PostInteraction interaction = interactions.getOrDefault(post.getId(), PostInteraction.EMPTY);
            items.add(new FeedItem(post.getId(), "POST", authors.get(post.getAuthorId()), post.getCommunityId(),
                    post.getTitle(), summary(post.getContent()), images.get(post.getId()), post.getCreatedAt(),
                    interaction.likeCount(), interaction.commentCount(), interaction.likedByMe()));
        }
        for (Listing listing : listingList) {
            items.add(new FeedItem(listing.getId(), "LISTING", authors.get(listing.getOwnerId()),
                    listing.getCommunityId(), listing.getTitle(), summary(listing.getDescription()),
                    listing.getImageUrl(), listing.getCreatedAt(), 0L, 0L, false));
        }
        for (HelpPost help : helpList) {
            items.add(new FeedItem(help.getId(), "HELP_POST", authors.get(help.getUserId()), null,
                    help.getTitle(), summary(help.getDescription()), null, help.getCreatedAt(), 0L, 0L, false));
        }

        items.sort(Comparator.comparing(FeedItem::createdAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return items.size() > size ? items.subList(0, size) : items;
    }

    private record PostInteraction(long likeCount, long commentCount, boolean likedByMe) {
        private static final PostInteraction EMPTY = new PostInteraction(0, 0, false);
    }

    private String summary(String text) {
        if (text == null) return null;
        return text.length() > 280 ? text.substring(0, 280) : text;
    }
}
