package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String avatarUrl;
    private Role role;
    private String studentId;
    private String teacherId;
    private boolean boundStatus;
    private BindingStatus bindingStatus;
    private LocalDateTime createdAt;
}
