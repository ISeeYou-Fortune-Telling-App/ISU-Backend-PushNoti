package com.iseeyou.fortunetelling.models;

import com.iseeyou.fortunetelling.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    private String id;

    @Field("notification_title")
    private String notificationTitle;

    @Field("target_type")
    private Constants.TargetType targetType;

    @Field("target_id")
    private String targetId;

    @Field("notification_body")
    private String notificationBody;

    @Field("meta_data")
    private Map<String, String> metaData;

    @Field("image_url")
    private String imageUrl;

    @Field("recipient_id")
    private String recipientId;

    @Field("is_read")
    private boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
