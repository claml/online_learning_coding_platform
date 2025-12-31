package com.example.onlinelearning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
