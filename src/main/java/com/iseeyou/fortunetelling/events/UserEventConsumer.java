package com.iseeyou.fortunetelling.events;

import com.iseeyou.fortunetelling.dtos.UserDeleteEvent;
import com.iseeyou.fortunetelling.dtos.UserLoginEvent;
import com.iseeyou.fortunetelling.dtos.UserLogoutEvent;
import com.iseeyou.fortunetelling.services.UserFcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserFcmTokenService userFcmTokenService;

    @RabbitListener(queues = "user.login.queue")
    public void handleUserLoginEvent(UserLoginEvent event) {
        log.info("Processing user login event for user: {}", event.getUserId());

        try {
            if (event.getUserId() == null || event.getUserId().isEmpty()) {
                log.warn("User ID is null or empty, skipping login event");
                return;
            }

            if (event.getFcmToken() == null || event.getFcmToken().isEmpty()) {
                log.warn("FCM token is null or empty, skipping login event for user: {}", event.getUserId());
                return;
            }

            userFcmTokenService.addFcmToken(event.getUserId(), event.getFcmToken());
            log.info("User login event processed successfully for user: {}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process user login event for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process user login event", e);
        }
    }

    @RabbitListener(queues = "user.logout.queue")
    public void handleUserLogoutEvent(UserLogoutEvent event) {
        log.info("Processing user logout event for user: {}", event.getUserId());

        try {
            if (event.getUserId() == null || event.getUserId().isEmpty()) {
                log.warn("User ID is null or empty, skipping logout event");
                return;
            }

            if (event.getFcmToken() == null || event.getFcmToken().isEmpty()) {
                log.warn("FCM token is null or empty, skipping logout event for user: {}", event.getUserId());
                return;
            }

            userFcmTokenService.removeFcmToken(event.getUserId(), event.getFcmToken());
            log.info("User logout event processed successfully for user: {}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process user logout event for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process user logout event", e);
        }
    }

    @RabbitListener(queues = "user.delete.queue")
    public void handleUserDeleteEvent(UserDeleteEvent event) {
        log.info("Processing user delete event for user: {}", event.getUserId());

        try {
            if (event.getUserId() == null || event.getUserId().isEmpty()) {
                log.warn("User ID is null or empty, skipping delete event");
                return;
            }

            userFcmTokenService.deleteUser(event.getUserId());
            log.info("User delete event processed successfully for user: {}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process user delete event for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process user delete event", e);
        }
    }
}

