package com.iseeyou.fortunetelling.events;

import com.iseeyou.fortunetelling.dtos.NotificationEvent;
import com.iseeyou.fortunetelling.services.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final PushNotificationService pushNotificationService;

    @RabbitListener(queues = "notification.queue")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Processing notification event: {} for recipient: {}",
                event.getTargetType().getValue(),
                event.getRecipientId() != null ? event.getRecipientId() : event.getFcmToken());

        try {
            sendToNotificationService(event);
        } catch (Exception e) {
            log.error("Failed to process notification event {}: {}", event.getEventId(), e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process notification event", e);
        }
    }

    private void sendToNotificationService(NotificationEvent event) {
        // Nếu không có cả recipientId và fcmToken thì skip
        if ((event.getRecipientId() == null || event.getRecipientId().isEmpty()) &&
                (event.getFcmToken() == null || event.getFcmToken().isEmpty())) {
            log.warn("Both recipientId and FCM token are null or empty, skipping notification event: {}", event.getEventId());
            return;
        }
        pushNotificationService.create(event);
    }
}