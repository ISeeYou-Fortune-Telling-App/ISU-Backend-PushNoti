package com.iseeyou.fortunetelling.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginEvent implements Serializable {
    private String userId;
    private String fcmToken;
}

