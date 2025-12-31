package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private Role role;
    private BindingStatus bindingStatus;
}
