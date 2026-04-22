package com.blog.postservice.service;

import com.blog.postservice.dto.PostRequest;
import com.blog.postservice.dto.PostResponse;
import com.blog.postservice.model.Post;
import com.blog.postservice.model.PostStatus;
import com.blog.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    // ─── CREATE ────────────────────────────────────────────────────────────────

    @Override
    public PostResponse createPost(PostRequest request, Long authorId) {
        Post post = Post.builder()
                .authorId(authorId)
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .featuredImageUrl(request.getFeaturedImageUrl())
                .status(PostStatus.DRAFT)
                .readTimeMin(computeReadTime(request.getContent()))
                .build();

        return toResponse(postRepository.save(post));
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    @Override
    public PostResponse getPostById(Long postId) {
        return toResponse(findPostOrThrow(postId));
    }

    @Override
    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Post not found with slug: " + slug));
        return toResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPublishedPosts() {
        return postRepository.findByStatusOrderByPublishedAtDesc(PostStatus.PUBLISHED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> searchPosts(String keyword) {
        return postRepository.searchByTitle(keyword)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    public PostResponse updatePost(Long postId, PostRequest request,
                                   Long requesterId, String requesterRole) {
        Post post = findPostOrThrow(postId);
        checkOwnerOrAdmin(post, requesterId, requesterRole, "update");

        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setReadTimeMin(computeReadTime(request.getContent()));

        return toResponse(postRepository.save(post));
    }

    // ─── PUBLISH / UNPUBLISH ───────────────────────────────────────────────────

    @Override
    public PostResponse publishPost(Long postId, Long requesterId, String requesterRole) {
        Post post = findPostOrThrow(postId);
        checkOwnerOrAdmin(post, requesterId, requesterRole, "publish");

        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        // 🔔 Newsletter notification goes here in Phase 2
        // newsletterClient.notifySubscribers(post);

        return toResponse(postRepository.save(post));
    }

    @Override
    public PostResponse unpublishPost(Long postId, Long requesterId, String requesterRole) {
        Post post = findPostOrThrow(postId);
        checkOwnerOrAdmin(post, requesterId, requesterRole, "unpublish");

        post.setStatus(PostStatus.UNPUBLISHED);
        return toResponse(postRepository.save(post));
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    @Override
    public void deletePost(Long postId, Long requesterId, String requesterRole) {
        Post post = findPostOrThrow(postId);
        checkOwnerOrAdmin(post, requesterId, requesterRole, "delete");
        postRepository.delete(post);
    }

    // ─── VIEWS & LIKES ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void incrementViews(Long postId) {
        findPostOrThrow(postId); // verify exists
        postRepository.incrementViewCount(postId); // atomic DB-level increment
    }

    @Override
    @Transactional
    public void likePost(Long postId) {
        findPostOrThrow(postId);
        postRepository.incrementLikesCount(postId);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        findPostOrThrow(postId);
        postRepository.decrementLikesCount(postId);
    }

    // ─── COUNT ─────────────────────────────────────────────────────────────────

    @Override
    public long getPostCount(Long authorId) {
        return postRepository.countByAuthorId(authorId);
    }

    // ─── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
    }

    // Only the post owner OR an ADMIN can modify/delete
    private void checkOwnerOrAdmin(Post post, Long requesterId,
                                   String requesterRole, String action) {
        boolean isOwner = post.getAuthorId().equals(requesterId);
        boolean isAdmin = "ROLE_ADMIN".equals(requesterRole);
        if (!isOwner && !isAdmin) {
            throw new RuntimeException(
                    "Access denied: you cannot " + action + " another author's post");
        }
    }

    // "My First Post!" → "my-first-post"
    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");

        // Add timestamp suffix to keep slugs unique
        return base + "-" + System.currentTimeMillis();
    }

    // ~200 words per minute average reading speed
    private int computeReadTime(String content) {
        if (content == null || content.isBlank()) return 1;
        int wordCount = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .authorId(post.getAuthorId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .excerpt(post.getExcerpt())
                .featuredImageUrl(post.getFeaturedImageUrl())
                .status(post.getStatus())
                .readTimeMin(post.getReadTimeMin())
                .viewCount(post.getViewCount())
                .likesCount(post.getLikesCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }
}