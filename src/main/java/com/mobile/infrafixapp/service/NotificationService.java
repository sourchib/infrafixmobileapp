package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.NotificationResponse;
import com.mobile.infrafixapp.model.Notification;
import com.mobile.infrafixapp.model.User;
import com.mobile.infrafixapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getUserNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(String.valueOf(notification.getId()))
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .type(notification.getType())
                .time(calculateRelativeTime(notification.getCreatedAt()))
                .build();
    }

    public void markNotificationAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private String calculateRelativeTime(LocalDateTime createdAt) {
        if (createdAt == null)
            return "Unknown";
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Baru saja";
        } else if (seconds < 3600) {
            return (seconds / 60) + " menit yang lalu";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " jam yang lalu";
        } else {
            return (seconds / 86400) + " hari yang lalu";
        }
    }
}
