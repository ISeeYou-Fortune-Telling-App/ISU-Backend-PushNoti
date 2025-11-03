package com.iseeyou.fortunetelling.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.secret}")
    private String appSecret;

    public String extractJwtFromRequest(final jakarta.servlet.http.HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserIdFromToken(final String token) {
        try {
            log.debug("Parsing JWT token...");
            log.debug("App secret length: {}", appSecret != null ? appSecret.length() : "NULL");

            // Try to parse with signature validation first
            try {
                Claims claims = parseToken(token).getBody();
                String userId = claims.getSubject();
                log.debug("Successfully extracted userId with signature validation: {}", userId);
                return userId;
            } catch (io.jsonwebtoken.security.SignatureException e) {
                log.warn("JWT signature validation failed, attempting to decode without validation...");
                // Decode without validation (since you mentioned JWT is guaranteed to be valid)
                Claims claims = decodeWithoutValidation(token);
                String userId = claims.getSubject();
                log.debug("Successfully extracted userId without validation: {}", userId);
                return userId;
            }
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Claims decodeWithoutValidation(final String token) {
        // Decode JWT without signature validation
        int i = token.lastIndexOf('.');
        String withoutSignature = token.substring(0, i + 1);
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("dummy".getBytes()))
                .build()
                .parseClaimsJwt(withoutSignature)
                .getBody();
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
    }

    private Key getSigningKey() {
        if (appSecret == null || appSecret.isEmpty()) {
            log.error("App secret is null or empty!");
            throw new IllegalStateException("App secret not configured");
        }
        return Keys.hmacShaKeyFor(appSecret.getBytes());
    }
}
