package com.iseeyou.fortunetelling.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Profile("!mvcIT")
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected final void doFilterInternal(@NonNull final HttpServletRequest request,
                                          @NonNull final HttpServletResponse response,
                                          @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.extractJwtFromRequest(request);

            if (StringUtils.hasText(token)) {
                log.debug("JWT token found, extracting userId...");
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                log.debug("UserId extracted from JWT: {}", userId);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, java.util.Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authentication set successfully for userId: {}", userId);
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}
