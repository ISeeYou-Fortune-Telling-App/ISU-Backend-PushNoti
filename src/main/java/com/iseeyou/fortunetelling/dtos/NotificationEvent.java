package com.iseeyou.fortunetelling.dtos;

import com.iseeyou.fortunetelling.utils.Constants;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class NotificationEvent {
    private String eventId;
    @NotNull
    private String fcmToken;
    private String notificationTitle;
    private Constants.TargetType targetType;
    private String targetId;
    private Map<String, String> metaData;
    private String imageUrl;
    private String notificationBody;
    private String recipientId;
}
