package com.mobile.infrafixapp.controller;

import com.mobile.infrafixapp.dto.NotificationResponse;
import com.mobile.infrafixapp.model.User;
import com.mobile.infrafixapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(@AuthenticationPrincipal User user) {
        return notificationService.getUserNotifications(user);
    }
}
