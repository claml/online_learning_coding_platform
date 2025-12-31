package com.example.onlinelearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostCommentResponse {
    private Long id;
    private Long postId;
    private Long authorId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;
}
