package com.example.onlinelearning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCommentRequest {
    @NotBlank
    private String content;
}
