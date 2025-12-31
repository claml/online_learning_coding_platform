package com.example.onlinelearning.controller;

import com.example.onlinelearning.dto.BindSubmitRequest;
import com.example.onlinelearning.dto.BindingRequestResponse;
import com.example.onlinelearning.service.BindingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserBindingController {

    private final BindingService bindingService;

    public UserBindingController(BindingService bindingService) {
        this.bindingService = bindingService;
    }

    @PostMapping("/bind")
    public ResponseEntity<BindingRequestResponse> bind(@Valid @RequestBody BindSubmitRequest request,
                                                       Authentication authentication) {
        BindingRequestResponse response = bindingService.submitBindingRequest(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
