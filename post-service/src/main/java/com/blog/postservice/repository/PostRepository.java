package com.blog.postservice.repository;

import com.blog.postservice.model.Post;
import com.blog.postservice.model.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByStatus(PostStatus status);

    // All posts by an author, newest first
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // All published posts, newest first
    List<Post> findByStatusOrderByPublishedAtDesc(PostStatus status);

    // Search posts by title keyword
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchByTitle(@Param("keyword") String keyword);

    // Count posts by author
    long countByAuthorId(Long authorId);

    // ✅ Atomic view increment — prevents concurrent update conflicts
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    // ✅ Atomic like increment
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.postId = :postId")
    void incrementLikesCount(@Param("postId") Long postId);

    // ✅ Atomic like decrement (min 0)
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = CASE WHEN p.likesCount > 0 THEN p.likesCount - 1 ELSE 0 END WHERE p.postId = :postId")
    void decrementLikesCount(@Param("postId") Long postId);
}