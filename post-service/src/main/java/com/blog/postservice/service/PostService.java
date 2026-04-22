package com.blog.postservice.service;

import com.blog.postservice.dto.PostRequest;
import com.blog.postservice.dto.PostResponse;
import java.util.List;

public interface PostService {

    PostResponse createPost(PostRequest request, Long authorId);

    PostResponse getPostById(Long postId);

    PostResponse getPostBySlug(String slug);

    List<PostResponse> getPostsByAuthor(Long authorId);

    List<PostResponse> getPublishedPosts();

    List<PostResponse> searchPosts(String keyword);

    PostResponse updatePost(Long postId, PostRequest request, Long requesterId, String requesterRole);

    PostResponse publishPost(Long postId, Long requesterId, String requesterRole);

    PostResponse unpublishPost(Long postId, Long requesterId, String requesterRole);

    void deletePost(Long postId, Long requesterId, String requesterRole);

    void incrementViews(Long postId);

    void likePost(Long postId);

    void unlikePost(Long postId);

    long getPostCount(Long authorId);
}