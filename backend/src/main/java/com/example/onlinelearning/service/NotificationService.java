package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.NotificationResponse;
import com.example.onlinelearning.entity.Notification;
import com.example.onlinelearning.entity.NotificationType;
import com.example.onlinelearning.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(User user, NotificationType type, String content) {
        Assert.notNull(user, "User is required");
        Assert.notNull(type, "Notification type is required");
        Assert.hasText(content, "Notification content is required");

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .content(content)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> listUserNotifications(String username) {
        User user = findUserOrThrow(username);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public NotificationResponse markAsRead(Long id, String username) {
        User user = findUserOrThrow(username);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Notification not found");
        }
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return toResponse(notification);
    }

    public long countUnread(String username) {
        User user = findUserOrThrow(username);
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
