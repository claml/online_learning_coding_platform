package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, NotificationType type, String content, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
