package com.iseeyou.fortunetelling.services.impl;

import com.iseeyou.fortunetelling.models.User;
import com.iseeyou.fortunetelling.repositories.UserRepository;
import com.iseeyou.fortunetelling.services.UserFcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserFcmTokenServiceImpl implements UserFcmTokenService {
    private final UserRepository userRepository;

    @Override
    public User addFcmToken(String userId, String fcmToken) {
        log.info("Adding FCM token for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUserId(userId);
                    return newUser;
                });

        user.addFcmToken(fcmToken);
        User savedUser = userRepository.save(user);

        log.info("FCM token added successfully. User {} now has {} token(s)",
                userId, savedUser.getFcmTokens().size());

        return savedUser;
    }

    @Override
    public User removeFcmToken(String userId, String fcmToken) {
        log.info("Removing FCM token for user: {}", userId);

        return userRepository.findByUserId(userId)
                .map(user -> {
                    user.removeFcmToken(fcmToken);
                    User savedUser = userRepository.save(user);
                    log.info("FCM token removed successfully. User {} now has {} token(s)",
                            userId, savedUser.getFcmTokens().size());
                    return savedUser;
                })
                .orElseGet(() -> {
                    log.warn("User {} not found, cannot remove FCM token", userId);
                    return null;
                });
    }

    @Override
    public void deleteUser(String userId) {
        log.info("Deleting user and all FCM tokens: {}", userId);

        userRepository.findByUserId(userId)
                .ifPresentOrElse(
                    user -> {
                        userRepository.delete(user);
                        log.info("User {} and all associated FCM tokens deleted successfully", userId);
                    },
                    () -> log.warn("User {} not found, cannot delete", userId)
                );
    }

    @Override
    public List<String> getFcmTokensByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(User::getFcmTokens)
                .orElse(Collections.emptyList());
    }
}

