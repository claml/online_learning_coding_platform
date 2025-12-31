package com.example.onlinelearning.controller;

import com.example.onlinelearning.dto.NotificationResponse;
import com.example.onlinelearning.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(notificationService.listUserNotifications(authentication.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(Authentication authentication) {
        long count = notificationService.countUnread(authentication.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(notificationService.markAsRead(id, authentication.getName()));
    }
}
