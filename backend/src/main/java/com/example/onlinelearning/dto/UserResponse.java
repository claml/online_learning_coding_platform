package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
    private BindingStatus bindingStatus;
}
