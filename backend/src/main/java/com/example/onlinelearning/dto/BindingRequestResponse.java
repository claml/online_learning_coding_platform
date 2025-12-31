package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BindingRequestResponse {
    private Long id;
    private Long userId;
    private String username;
    private Role role;
    private String identifier;
    private BindingStatus status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String reviewerUsername;
}
