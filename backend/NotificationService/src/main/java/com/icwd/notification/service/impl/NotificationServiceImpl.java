package com.icwd.notification.service.impl;

import com.icwd.notification.entity.Notification;
import com.icwd.notification.repository.NotificationRepository;
import com.icwd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(
            Notification notification
    ) {

        log.info(
                "Starting notification creation"
        );

        String notificationId =
                UUID.randomUUID().toString();

        notification.setNotificationId(
                notificationId
        );

        log.debug(
                "Generated notification ID. notificationId={}",
                notificationId
        );

        notification.setIsRead(
                false
        );

        log.debug(
                "Notification read status initialized to false. notificationId={}",
                notificationId
        );

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        log.info(
                "Notification created successfully. notificationId={}",
                savedNotification.getNotificationId()
        );

        return savedNotification;
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(
            String id
    ) {

        log.debug(
                "Fetching notification by ID. notificationId={}",
                id
        );

        return notificationRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Notification not found. notificationId={}",
                            id
                    );

                    return new RuntimeException(
                            "Notification not found with id: "
                                    + id
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {

        log.debug(
                "Fetching all notifications from repository"
        );

        List<Notification> notifications =
                notificationRepository
                        .findAll();

        log.info(
                "Notifications fetched successfully. count={}",
                notifications.size()
        );

        return notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getByUserId(
            String userId
    ) {

        log.debug(
                "Fetching notifications by user. userId={}",
                userId
        );

        List<Notification> notifications =
                notificationRepository
                        .findByUserId(
                                userId
                        );

        log.info(
                "Notifications fetched successfully for user. userId={}, count={}",
                userId,
                notifications.size()
        );

        return notifications;
    }

    @Override
    public Notification markAsRead(
            String id
    ) {

        log.info(
                "Starting notification read-status update. notificationId={}",
                id
        );

        Notification notification =
                getNotificationById(
                        id
                );

        notification.setIsRead(
                true
        );

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        log.info(
                "Notification marked as read successfully. notificationId={}",
                id
        );

        return savedNotification;
    }

    @Override
    public void markAllAsRead() {

        log.info(
                "Starting bulk update to mark all notifications as read"
        );

        notificationRepository
                .markAllAsRead();

        log.info(
                "All notifications marked as read successfully"
        );
    }
}