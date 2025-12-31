package com.example.onlinelearning.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(max = 255, message = "Avatar URL must be at most 255 characters")
    private String avatarUrl;
}
