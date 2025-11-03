package com.iseeyou.fortunetelling.services;

import com.iseeyou.fortunetelling.security.JwtUserDetails;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    UUID getCurrentUserId();
    JwtUserDetails getPrincipal(Authentication authentication);
}
