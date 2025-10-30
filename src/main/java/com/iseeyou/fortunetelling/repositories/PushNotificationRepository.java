package com.iseeyou.fortunetelling.repositories;

import com.iseeyou.fortunetelling.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationRepository extends MongoRepository<Notification, String> {
    Page<Notification> getNotificationsByRecipientId(String recipientId, Pageable pageable);
}
