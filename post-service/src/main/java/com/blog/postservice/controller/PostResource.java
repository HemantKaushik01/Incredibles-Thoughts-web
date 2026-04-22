package com.blog.postservice.controller;

import com.blog.postservice.dto.PostRequest;
import com.blog.postservice.dto.PostResponse;
import com.blog.postservice.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostResource {

    private final PostService postService;

    // ── CREATE — WRITER or ADMIN only ──────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            HttpServletRequest httpRequest) {

        Long authorId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, authorId));
    }

    // ── READ endpoints (public) ────────────────────────────────────────────────
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostResponse>> getPublished() {
        return ResponseEntity.ok(postService.getPublishedPosts());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostResponse>> getByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    @GetMapping("/count/{authorId}")
    public ResponseEntity<Long> getCount(@PathVariable Long authorId) {
        return ResponseEntity.ok(postService.getPostCount(authorId));
    }

    // ── UPDATE — owner or ADMIN ────────────────────────────────────────────────
    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest request,
            HttpServletRequest httpRequest) {

        Long requesterId = (Long) httpRequest.getAttribute("userId");
        String role      = (String) httpRequest.getAttribute("userRole");
        return ResponseEntity.ok(postService.updatePost(postId, request, requesterId, role));
    }

    // ── PUBLISH ────────────────────────────────────────────────────────────────
    @PutMapping("/{postId}/publish")
    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    public ResponseEntity<PostResponse> publish(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {

        Long requesterId = (Long) httpRequest.getAttribute("userId");
        String role      = (String) httpRequest.getAttribute("userRole");
        return ResponseEntity.ok(postService.publishPost(postId, requesterId, role));
    }

    // ── UNPUBLISH ──────────────────────────────────────────────────────────────
    @PutMapping("/{postId}/unpublish")
    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    public ResponseEntity<PostResponse> unpublish(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {

        Long requesterId = (Long) httpRequest.getAttribute("userId");
        String role      = (String) httpRequest.getAttribute("userRole");
        return ResponseEntity.ok(postService.unpublishPost(postId, requesterId, role));
    }

    // ── DELETE — owner or ADMIN ────────────────────────────────────────────────
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('WRITER','ADMIN')")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {

        Long requesterId = (Long) httpRequest.getAttribute("userId");
        String role      = (String) httpRequest.getAttribute("userRole");
        postService.deletePost(postId, requesterId, role);
        return ResponseEntity.noContent().build();
    }

    // ── VIEWS (public, atomic) ─────────────────────────────────────────────────
    @PostMapping("/{postId}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long postId) {
        postService.incrementViews(postId);
        return ResponseEntity.ok().build();
    }

    // ── LIKES ──────────────────────────────────────────────────────────────────
    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlike(@PathVariable Long postId) {
        postService.unlikePost(postId);
        return ResponseEntity.ok().build();
    }
}