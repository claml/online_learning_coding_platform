package com.example.onlinelearning.controller;

import com.example.onlinelearning.dto.BindingRequestResponse;
import com.example.onlinelearning.dto.BindingReviewRequest;
import com.example.onlinelearning.service.BindingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bindings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBindingController {

    private final BindingService bindingService;

    public AdminBindingController(BindingService bindingService) {
        this.bindingService = bindingService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BindingRequestResponse>> pendingRequests() {
        return ResponseEntity.ok(bindingService.findPendingRequests());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BindingRequestResponse> approve(@PathVariable("id") Long id,
                                                          Authentication authentication) {
        BindingRequestResponse response = bindingService.approve(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BindingRequestResponse> reject(@PathVariable("id") Long id,
                                                         @Valid @RequestBody BindingReviewRequest request,
                                                         Authentication authentication) {
        BindingRequestResponse response = bindingService.reject(id, authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
