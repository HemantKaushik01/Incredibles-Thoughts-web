package com.blog.postservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private Long authorId;          // references user.id from user-service

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;            // auto-generated from title e.g. "my-first-post"

    @Column(columnDefinition = "TEXT")
    private String content;         // rich HTML content

    private String excerpt;         // short summary shown in feed

    private String featuredImageUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;

    private Integer readTimeMin;    // computed from content word count

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Long likesCount = 0L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}