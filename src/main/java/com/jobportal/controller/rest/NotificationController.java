package com.jobportal.controller.rest;

import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//public class NotificationController {
//    NotificationService notificationService;
//
//    @GetMapping("/api/notifications")
//    public List<Notification> getNotifications(@AuthenticationPrincipal User user) {
//        return notificationService.getNotifications(user.getId());
//    }
//
//    @PostMapping("/api/notifications/read-all")
//    public void markAllAsRead(@AuthenticationPrincipal User user) {
//        notificationService.markAllAsRead(user.getId());
//    }
//
//    @PostMapping("/api/notifications/read/{id}")
//    public void markOneAsRead(@PathVariable Integer id) {
//        notificationService.markOneAsRead(id);
//    }
//
//    @GetMapping("/api/notifications/unread-count")
//    public long getUnreadCount(@AuthenticationPrincipal User user) {
//        return notificationService.countNotSeen(user.getId());
//    }
//}
