package com.iseeyou.fortunetelling.services.impl;

import com.google.firebase.messaging.*;
import com.iseeyou.fortunetelling.dtos.NotificationEvent;
import com.iseeyou.fortunetelling.exceptions.ResourceNotFoundException;
import com.iseeyou.fortunetelling.models.Notification;
import com.iseeyou.fortunetelling.repositories.PushNotificationRepository;
import com.iseeyou.fortunetelling.services.PushNotificationService;
import com.iseeyou.fortunetelling.services.UserFcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {
    private final PushNotificationRepository pushNotificationRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final com.iseeyou.fortunetelling.services.AuthService authService;
    private final UserFcmTokenService userFcmTokenService;

    @Override
    public Notification create(NotificationEvent notificationEvent) {
        Notification newNotification = new Notification();

        newNotification.setNotificationTitle(notificationEvent.getNotificationTitle());
        newNotification.setNotificationBody(notificationEvent.getNotificationBody());
        newNotification.setRecipientId(notificationEvent.getRecipientId());
        newNotification.setTargetId(notificationEvent.getTargetId());
        newNotification.setTargetType(notificationEvent.getTargetType());
        newNotification.setImageUrl(notificationEvent.getImageUrl());
        
        // Gửi notification đến FCM token(s)
        sendNotificationToUser(notificationEvent);

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

    /**
     * Gửi notification đến user. Nếu có recipientId, sẽ lấy tất cả FCM tokens của user đó.
     * Nếu không có recipientId nhưng có fcmToken, sẽ gửi trực tiếp đến token đó.
     */
    private void sendNotificationToUser(NotificationEvent event) {
        String title = event.getNotificationTitle();
        String body = event.getNotificationBody();
        String imageUrl = event.getImageUrl();

        // Nếu có recipientId, lấy tất cả FCM tokens của user
        if (event.getRecipientId() != null && !event.getRecipientId().isEmpty()) {
            List<String> fcmTokens = userFcmTokenService.getFcmTokensByUserId(event.getRecipientId());

            if (fcmTokens.isEmpty()) {
                log.warn("No FCM tokens found for user: {}", event.getRecipientId());

                // Nếu có fcmToken trong event, vẫn thử gửi
                if (event.getFcmToken() != null && !event.getFcmToken().isEmpty()) {
                    sendPushNotification(event.getFcmToken(), title, body, imageUrl);
                }
            } else {
                log.info("Sending notification to {} device(s) for user: {}",
                        fcmTokens.size(), event.getRecipientId());

                // Gửi đến tất cả tokens của user
                for (String token : fcmTokens) {
                    sendPushNotification(token, title, body, imageUrl);
                }
            }
        }
        // Nếu không có recipientId nhưng có fcmToken, gửi trực tiếp
        else if (event.getFcmToken() != null && !event.getFcmToken().isEmpty()) {
            sendPushNotification(event.getFcmToken(), title, body, imageUrl);
        }
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
            log.info("Successfully sent notification to: {}", fcmToken);
            log.debug("FCM Response: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to token: {}", fcmToken);
            log.error("Error code: {}, message: {}", e.getErrorCode(), e.getMessage());

            // Handle specific error cases
        } catch (Exception e) {
            log.error("Unexpected error sending notification: {}", e.getMessage(), e);
        }
    }
}
