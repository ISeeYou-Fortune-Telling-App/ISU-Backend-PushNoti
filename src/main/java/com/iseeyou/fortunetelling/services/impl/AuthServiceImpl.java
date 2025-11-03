package com.iseeyou.fortunetelling.services.impl;

import com.iseeyou.fortunetelling.security.JwtUserDetails;
import com.iseeyou.fortunetelling.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Override
    @Transactional
    public UUID getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Object principal = authentication.getPrincipal();

                // Check if principal is a String
                if (!(principal instanceof String)) {
                    log.warn("[JWT] Principal is not a String: {}", principal.getClass().getName());
                    throw new BadCredentialsException("Bad credentials");
                }

                String userId = (String) principal;

                // Check if user is anonymous
                if ("anonymousUser".equals(userId)) {
                    log.warn("[JWT] Anonymous user detected - JWT token missing or invalid");
                    throw new BadCredentialsException("Authentication required - JWT token missing or invalid");
                }

                return UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                log.warn("[JWT] Invalid UUID format: {}", authentication.getPrincipal());
                throw new BadCredentialsException("Invalid user ID format");
            }
        } else {
            log.warn("[JWT] User not authenticated!");
            throw new BadCredentialsException("Bad credentials");
        }
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    @Transactional(readOnly = true)
    public JwtUserDetails getPrincipal(final Authentication authentication) {
        return (JwtUserDetails) authentication.getPrincipal();
    }
}
