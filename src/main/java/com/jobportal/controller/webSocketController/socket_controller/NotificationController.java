package com.jobportal.controller.webSocketController.socket_controller;

import com.jobportal.dto.response.NotificationDTO;
import com.jobportal.entity.Intern;
import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.service.NotificationService;
import com.jobportal.service.UserService;
import com.jobportal.service.intern.InternService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@RequiredArgsConstructor
@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NotificationController {
    NotificationService notificationService;
    InternService internService;
    UserService userService;
    @GetMapping("/api/notifications")
    public List<NotificationDTO> getNotifications(Principal principal ) {
        User u = userService.getUser(principal);
        return notificationService. getNotifications(u.getId());
    }

    @PostMapping("/api/notifications/read-all")
    public void markAllAsRead(Principal principal) {
        User u = userService.getUser(principal);
        notificationService.markAllAsRead(u.getId());
    }

    @PostMapping("/api/notifications/read/{id}")
    public void markOneAsRead(@PathVariable Integer id) {
        notificationService.markOneAsRead(id);
    }

    @GetMapping("/api/notifications/unread-count")
    public long getUnreadCount(Principal principal) {
        User u = userService.getUser(principal);
        return notificationService.countNotSeen(u.getId());
    }
}
