package com.iseeyou.fortunetelling.services.impl;

import com.google.firebase.messaging.*;
import com.iseeyou.fortunetelling.dtos.NotificationEvent;
import com.iseeyou.fortunetelling.exceptions.ResourceNotFoundException;
import com.iseeyou.fortunetelling.models.Notification;
import com.iseeyou.fortunetelling.repositories.PushNotificationRepository;
import com.iseeyou.fortunetelling.services.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {
    private final PushNotificationRepository pushNotificationRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final com.iseeyou.fortunetelling.services.AuthService authService;

    @Override
    public Notification create(NotificationEvent notificationEvent) {
        Notification newNotification = new Notification();

        newNotification.setNotificationTitle(notificationEvent.getNotificationTitle());
        newNotification.setNotificationBody(notificationEvent.getNotificationBody());
        newNotification.setRecipientId(notificationEvent.getRecipientId());
        newNotification.setTargetId(notificationEvent.getTargetId());
        newNotification.setTargetType(notificationEvent.getTargetType());
        newNotification.setImageUrl(notificationEvent.getImageUrl());
        
        sendPushNotification(
                notificationEvent.getFcmToken(),
                notificationEvent.getNotificationTitle(),
                notificationEvent.getNotificationBody(),
                notificationEvent.getImageUrl()
        );

        return pushNotificationRepository.save(newNotification);
    }

    @Override
    public Notification read(String notificationId) {
        Notification notification = pushNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        return pushNotificationRepository.save(notification);
    }

    @Override
    public void delete(String notificationId) {
        pushNotificationRepository.deleteById(notificationId);
    }

    @Override
    public Page<Notification> getNotificationsByRecipientId(String recipientId, Pageable pageable) {
        return pushNotificationRepository.getNotificationsByRecipientId(recipientId, pageable);
    }

    @Override
    public Page<Notification> getAllMyNotifications(Pageable pageable) {
        String currentUserId = authService.getCurrentUserId().toString();
        return getNotificationsByRecipientId(currentUserId, pageable);
    }

    private void sendPushNotification(String fcmToken, String title, String body, String imageUrl) {
        try {
            com.google.firebase.messaging.Notification firebaseNotification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Add data payload for handling when app is in background
            Map<String, String> data = new HashMap<>();
            data.put("click_action", "OPEN_ACTIVITY");
            data.put("title", title);
            data.put("body", body);

            Message pushNotification = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(firebaseNotification)
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId("default_channel")
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = firebaseMessaging.send(pushNotification);
            System.out.println("Successfully sent notification to: " + fcmToken);
            System.out.println("FCM Response: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send notification to token: " + fcmToken);
            System.err.println("Error code: " + e.getErrorCode() + ", message: " + e.getMessage());

            // Handle specific error cases
        } catch (Exception e) {
            System.err.println("Unexpected error sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
