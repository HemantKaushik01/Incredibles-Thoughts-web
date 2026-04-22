package com.blog.postservice.dto;

import com.blog.postservice.model.PostStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long postId;
    private Long authorId;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String featuredImageUrl;
    private PostStatus status;
    private Integer readTimeMin;
    private Long viewCount;
    private Long likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}