package com.blog.postservice.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;       // rich HTML
    private String excerpt;
    private String featuredImageUrl;
}