package com.oognuyh.kanban.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifiedResult {
    
    private String email;
    
    private boolean isSuccessful;

    private Collection<GrantedAuthority> authorities;

    public static VerifiedResult OK(String email, String[] authorities) {
        return VerifiedResult.builder()
            .isSuccessful(true)
            .email(email)
            .authorities(Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }

    public static VerifiedResult ERROR(String email, String[] authorities) {
        return VerifiedResult.builder()
            .isSuccessful(false)
            .email(email)
            .authorities(Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }
}
