package com.iseeyou.fortunetelling.services;

import com.iseeyou.fortunetelling.models.User;

import java.util.List;

public interface UserFcmTokenService {
    User addFcmToken(String userId, String fcmToken);
    User removeFcmToken(String userId, String fcmToken);
    void deleteUser(String userId);
    List<String> getFcmTokensByUserId(String userId);
}

