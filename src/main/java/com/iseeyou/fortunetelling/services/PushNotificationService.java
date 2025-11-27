package com.iseeyou.fortunetelling.services;

import com.iseeyou.fortunetelling.dtos.NotificationEvent;
import com.iseeyou.fortunetelling.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PushNotificationService {
    Notification create(NotificationEvent notification);
    Notification read(String notificationId);
    void delete(String notificationId);
    Page<Notification> getNotificationsByRecipientId(String recipientId, Pageable pageable);
    Page<Notification> getAllMyNotifications(Pageable pageable);
}
