package com.iseeyou.fortunetelling.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeleteEvent implements Serializable {
    private String userId;
}

