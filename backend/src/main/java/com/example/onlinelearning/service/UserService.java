package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.PasswordUpdateRequest;
import com.example.onlinelearning.dto.UpdateUserProfileRequest;
import com.example.onlinelearning.dto.UserProfileResponse;
import com.example.onlinelearning.entity.BindingStatus;
import com.example.onlinelearning.entity.Role;
import com.example.onlinelearning.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getCurrentUserProfile(String username) {
        User user = findUserOrThrow(username);
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateUserProfileRequest request) {
        User user = findUserOrThrow(username);
        if (StringUtils.hasText(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void updatePassword(String username, PasswordUpdateRequest request) {
        User user = findUserOrThrow(username);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse toResponse(User user) {
        boolean bound = user.getBindingStatus() == BindingStatus.BOUND;
        String studentId = user.getRole() == Role.STUDENT ? user.getStudentId() : null;
        String teacherId = user.getRole() == Role.TEACHER ? user.getTeacherId() : null;
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .studentId(studentId)
                .teacherId(teacherId)
                .boundStatus(bound)
                .bindingStatus(user.getBindingStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
