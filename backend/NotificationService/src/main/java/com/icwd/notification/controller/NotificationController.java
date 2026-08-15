package com.icwd.notification.controller;

import com.icwd.notification.entity.Notification;
import com.icwd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/notifications", "/api/notifications"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification
    ) {

        log.info(
                "Received request to create notification"
        );

        Notification createdNotification =
                notificationService.createNotification(
                        notification
                );

        log.info(
                "Notification created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdNotification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam(required = false) String userId
    ) {

        if (userId != null) {

            log.info(
                    "Received request to fetch notifications by user. userId={}",
                    userId
            );

            List<Notification> notifications =
                    notificationService
                            .getByUserId(
                                    userId
                            );

            log.debug(
                    "Notifications fetched successfully for user. userId={}, count={}",
                    userId,
                    notifications.size()
            );

            return ResponseEntity.ok(
                    notifications
            );
        }

        log.info(
                "Received request to fetch all notifications"
        );

        List<Notification> notifications =
                notificationService
                        .getAllNotifications();

        log.debug(
                "Fetched all notifications successfully. count={}",
                notifications.size()
        );

        return ResponseEntity.ok(
                notifications
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable String id
    ) {

        log.info(
                "Received request to mark notification as read. notificationId={}",
                id
        );

        Notification updatedNotification =
                notificationService
                        .markAsRead(
                                id
                        );

        log.info(
                "Notification marked as read successfully. notificationId={}",
                id
        );

        return ResponseEntity.ok(
                updatedNotification
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {

        log.info(
                "Received request to mark all notifications as read"
        );

        notificationService.markAllAsRead();

        log.info(
                "All notifications marked as read successfully"
        );

        return ResponseEntity.ok(
                Map.of(
                        "success",
                        true,
                        "message",
                        "All notifications marked as read"
                )
        );
    }
}