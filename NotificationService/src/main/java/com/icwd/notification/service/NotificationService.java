package com.icwd.notification.service;

import com.icwd.notification.entity.Notification;
import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Notification getNotificationById(String id);
    List<Notification> getAllNotifications();
    List<Notification> getByUserId(String userId);
    Notification markAsRead(String id);
    void markAllAsRead();
}
