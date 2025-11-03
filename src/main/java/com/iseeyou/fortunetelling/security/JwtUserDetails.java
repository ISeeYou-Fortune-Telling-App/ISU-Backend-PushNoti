package com.iseeyou.fortunetelling.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@RequiredArgsConstructor
public final class JwtUserDetails implements UserDetails {
    private String id;

    private String email;

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;
}
