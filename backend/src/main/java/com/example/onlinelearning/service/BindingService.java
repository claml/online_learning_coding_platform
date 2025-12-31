package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.BindSubmitRequest;
import com.example.onlinelearning.dto.BindingRequestResponse;
import com.example.onlinelearning.dto.BindingReviewRequest;
import com.example.onlinelearning.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BindingService {

    private final UserRepository userRepository;
    private final BindingRequestRepository bindingRequestRepository;

    public BindingService(UserRepository userRepository, BindingRequestRepository bindingRequestRepository) {
        this.userRepository = userRepository;
        this.bindingRequestRepository = bindingRequestRepository;
    }

    @Transactional
    public BindingRequestResponse submitBindingRequest(String username, BindSubmitRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN does not require binding");
        }
        if (user.getBindingStatus() == BindingStatus.PENDING) {
            throw new IllegalStateException("There is already a pending binding request");
        }
        if (user.getBindingStatus() == BindingStatus.BOUND) {
            throw new IllegalStateException("User is already bound");
        }

        BindingRequest bindingRequest = new BindingRequest();
        bindingRequest.setUser(user);
        bindingRequest.setIdentifier(request.getIdentifier());
        bindingRequest.setStatus(BindingStatus.PENDING);
        bindingRequestRepository.save(bindingRequest);

        user.setBindingStatus(BindingStatus.PENDING);
        userRepository.save(user);

        return toResponse(bindingRequest);
    }

    public List<BindingRequestResponse> findPendingRequests() {
        return bindingRequestRepository.findAllByStatusOrderByCreatedAtAsc(BindingStatus.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public BindingRequestResponse approve(Long requestId, String reviewerUsername) {
        BindingRequest bindingRequest = bindingRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Binding request not found"));
        validatePending(bindingRequest);
        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        bindingRequest.setStatus(BindingStatus.BOUND);
        bindingRequest.setReviewedAt(LocalDateTime.now());
        bindingRequest.setReviewer(reviewer);
        bindingRequest.setRejectReason(null);

        User user = bindingRequest.getUser();
        user.setBindingStatus(BindingStatus.BOUND);
        user.setBindingIdentifier(bindingRequest.getIdentifier());
        user.setBindingUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return toResponse(bindingRequestRepository.save(bindingRequest));
    }

    @Transactional
    public BindingRequestResponse reject(Long requestId, String reviewerUsername, BindingReviewRequest reviewRequest) {
        Assert.hasText(reviewRequest.getReason(), "Reject reason is required");
        BindingRequest bindingRequest = bindingRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Binding request not found"));
        validatePending(bindingRequest);
        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        bindingRequest.setStatus(BindingStatus.REJECTED);
        bindingRequest.setReviewedAt(LocalDateTime.now());
        bindingRequest.setReviewer(reviewer);
        bindingRequest.setRejectReason(reviewRequest.getReason());

        User user = bindingRequest.getUser();
        user.setBindingStatus(BindingStatus.REJECTED);
        user.setBindingIdentifier(null);
        userRepository.save(user);

        return toResponse(bindingRequestRepository.save(bindingRequest));
    }

    private void validatePending(BindingRequest bindingRequest) {
        if (bindingRequest.getStatus() != BindingStatus.PENDING) {
            throw new IllegalStateException("Binding request has already been reviewed");
        }
    }

    private BindingRequestResponse toResponse(BindingRequest request) {
        return BindingRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .username(request.getUser().getUsername())
                .role(request.getUser().getRole())
                .identifier(request.getIdentifier())
                .status(request.getStatus())
                .rejectReason(request.getRejectReason())
                .createdAt(request.getCreatedAt())
                .reviewedAt(request.getReviewedAt())
                .reviewerUsername(request.getReviewer() != null ? request.getReviewer().getUsername() : null)
                .build();
    }
}
